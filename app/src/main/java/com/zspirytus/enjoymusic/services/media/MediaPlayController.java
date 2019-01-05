package com.zspirytus.enjoymusic.services.media;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;

import com.zspirytus.enjoymusic.cache.CurrentPlayingMusicCache;
import com.zspirytus.enjoymusic.cache.MusicSharedPreferences;
import com.zspirytus.enjoymusic.cache.PlayHistoryCache;
import com.zspirytus.enjoymusic.engine.MusicPlayOrderManager;
import com.zspirytus.enjoymusic.entity.Music;
import com.zspirytus.enjoymusic.global.MainApplication;
import com.zspirytus.enjoymusic.listeners.observable.MusicStateObservable;
import com.zspirytus.enjoymusic.services.NotificationHelper;
import com.zspirytus.enjoymusic.utils.LogUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音乐播放暂停控制类
 * Created by ZSpirytus on 2018/8/10.
 */

public class MediaPlayController extends MusicStateObservable
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final int STATE_IDLE = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_PREPARING = 2;
    private static final int STATE_PREPARED = 3;
    private static final int STATE_STARTED = 4;
    private static final int STATE_PAUSED = 5;
    private static final int STATE_PLAYBACK_COMPLETED = 6;

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private PlayTimer mPlayingTimer;

    private int state;

    private Music requestedToPlayMusic;
    private Music currentPlayingMusic;

    private static final MediaPlayController INSTANCE = new MediaPlayController();

    private MediaPlayController() {
        // init MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setWakeMode(MainApplication.getBackgroundContext(), PowerManager.PARTIAL_WAKE_LOCK);
        audioManager = (AudioManager) MainApplication.getBackgroundContext().getSystemService(Service.AUDIO_SERVICE);

        // set listeners
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        // init timer
        mPlayingTimer = new PlayTimer();

        // set MediaPlayer State
        state = STATE_IDLE;
    }

    public static MediaPlayController getInstance() {
        return INSTANCE;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        beginPlay();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        state = STATE_PLAYBACK_COMPLETED;
        Music nextMusic = MusicPlayOrderManager.getInstance().getNextMusic(false);
        if (nextMusic != null) {
            play(nextMusic);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // pause
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                // play
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                //stop
                break;
        }
    }

    public boolean isPlaying() {
        return state == STATE_STARTED;
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void play(Music music) {
        try {
            requestedToPlayMusic = music;
            if (currentPlayingMusic != null && currentPlayingMusic.equals(requestedToPlayMusic)) {
                // selected music is currently playing or pausing or has not prepared
                if (state >= STATE_PREPARED) {
                    // if has prepared, play it
                    if (!isPlaying()) {
                        beginPlay();
                    }
                } else if (state == STATE_IDLE) {
                    // else if has not in preparing, prepare it
                    prepareMusic(requestedToPlayMusic);
                }
            } else {
                // selected music is NOT currently playing or pausing
                prepareMusic(requestedToPlayMusic);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (state == STATE_STARTED) {
            mediaPlayer.pause();
            state = STATE_PAUSED;
            mPlayingTimer.pause();
            NotificationHelper.getInstance().updateNotification(false);
            notifyAllObserverPlayStateChange(false);
            MyMediaSession.getInstance().setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            MyMediaSession.getInstance().setPlaybackState(PlaybackStateCompat.STATE_STOPPED);
        }
    }

    private void prepareMusic(Music music) throws IOException {
        mPlayingTimer.pause();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(music.getMusicFilePath());
        state = STATE_INITIALIZED;
        mediaPlayer.prepareAsync();
        state = STATE_PREPARING;
        MyMediaSession.getInstance().setPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
    }

    private void beginPlay() {
        if (currentPlayingMusic == null || !requestedToPlayMusic.equals(currentPlayingMusic)) {
            notifyAllObserverPlayMusicChange(requestedToPlayMusic);
        }
        currentPlayingMusic = requestedToPlayMusic;
        MyMediaSession.getInstance().setMetaData(currentPlayingMusic);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        mediaPlayer.start();
        state = STATE_STARTED;
        mPlayingTimer.start();
        NotificationHelper.getInstance().showNotification(currentPlayingMusic);
        NotificationHelper.getInstance().updateNotification(true);
        notifyAllObserverPlayStateChange(true);
        MyMediaSession.getInstance().setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
        CurrentPlayingMusicCache.getInstance().setCurrentPlayingMusic(currentPlayingMusic);
        PlayHistoryCache.getInstance().add(currentPlayingMusic);
    }

    /**
     * Timer
     */
    private static class PlayTimer {

        private static Timer mTimer;
        private static TimerTask mTimerTask;

        private boolean hasStart = false;

        public void start() {
            hasStart = true;
            final int SECONDS = 1000;
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    int currentPlayingSeconds = INSTANCE.getCurrentPosition() / 1000;
                    INSTANCE.notifyAllObserverMusicPlayProgressChange(currentPlayingSeconds);
                }
            };
            mTimer.schedule(mTimerTask, 0, SECONDS);
        }

        public void pause() {
            if (hasStart) {
                mTimer.cancel();
                mTimer = null;
                mTimerTask.cancel();
                mTimerTask = null;
                hasStart = false;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        MusicSharedPreferences.saveMusic(currentPlayingMusic);
        LogUtil.e(this.getClass().getSimpleName(), "MediaPlayController will be destroy!");
    }
}
