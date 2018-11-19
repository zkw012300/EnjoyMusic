package com.zspirytus.enjoymusic.engine;

import android.content.Context;
import android.content.SharedPreferences;

import com.zspirytus.enjoymusic.adapter.binder.IMusicPlayCompleteObserverImpl;
import com.zspirytus.enjoymusic.cache.CurrentPlayingMusicCache;
import com.zspirytus.enjoymusic.cache.MyApplication;
import com.zspirytus.enjoymusic.entity.Music;
import com.zspirytus.enjoymusic.receivers.observer.MusicPlayCompleteObserver;
import com.zspirytus.enjoymusic.utils.RandomUtil;

import java.util.List;

/**
 * Created by ZSpirytus on 2018/9/5.
 */

public class MusicPlayOrderManager implements MusicPlayCompleteObserver {

    public static final int MODE_ORDER = 1;
    public static final int MODE_RANDOM = 2;
    public static final int MODE_LOOP = 4;

    private static final MusicPlayOrderManager INSTANCE = new MusicPlayOrderManager();
    private static final String MODE_FILE_NAME = "play_mode";
    private static final String MODE_KEY = "mode_key";

    private List<Music> mPlayList;

    private int mMode;

    private MusicPlayOrderManager() {
        IMusicPlayCompleteObserverImpl.getInstance().register(this);
        restoreMode();
        setMode(MODE_ORDER);
    }

    public static MusicPlayOrderManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void onMusicPlayComplete() {

    }

    public void setPlayList(List<Music> playList) {
        mPlayList = playList;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public Music getNextMusic() {
        Music nextMusic;
        switch (mMode) {
            case MODE_RANDOM:
                nextMusic = mPlayList.get(RandomUtil.rand(mPlayList.size()));
                break;
            case MODE_LOOP:
                nextMusic = CurrentPlayingMusicCache.getInstance().getCurrentPlayingMusic();
                break;
            default:
                int currentPosition = mPlayList.indexOf(CurrentPlayingMusicCache.getInstance().getCurrentPlayingMusic());
                int nextPosition = (currentPosition + 1) % mPlayList.size();
                nextMusic = mPlayList.get(nextPosition);
                break;
        }
        return nextMusic != null ? nextMusic : mPlayList.get(0);
    }

    public Music getPreviousMusic() {
        Music previousMusic;
        switch (mMode) {
            case MODE_RANDOM:
                previousMusic = mPlayList.get(RandomUtil.rand(mPlayList.size()));
                break;
            case MODE_LOOP:
                previousMusic = CurrentPlayingMusicCache.getInstance().getCurrentPlayingMusic();
                break;
            default:
                int currentPosition = mPlayList.indexOf(CurrentPlayingMusicCache.getInstance().getCurrentPlayingMusic());
                int previousPosition = currentPosition - 1 >= 0 ? currentPosition - 1 : currentPosition - 1 + mPlayList.size();
                previousMusic = mPlayList.get(previousPosition);
                break;
        }
        return previousMusic != null ? previousMusic : mPlayList.get(0);
    }

    private void saveMode() {
        SharedPreferences.Editor editor = MyApplication.getGlobalContext().getSharedPreferences(MODE_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(MODE_KEY, mMode);
        editor.apply();
    }

    private int restoreMode() {
        SharedPreferences pref = MyApplication.getGlobalContext().getSharedPreferences(MODE_FILE_NAME, Context.MODE_PRIVATE);
        int mode = pref.getInt(MODE_KEY, MODE_ORDER);
        return mode;
    }

}
