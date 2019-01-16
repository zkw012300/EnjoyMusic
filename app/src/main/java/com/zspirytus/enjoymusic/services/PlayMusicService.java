package com.zspirytus.enjoymusic.services;

import android.app.Notification;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;

import com.zspirytus.enjoymusic.base.BaseService;
import com.zspirytus.enjoymusic.cache.CurrentPlayingMusicCache;
import com.zspirytus.enjoymusic.cache.MusicSharedPreferences;
import com.zspirytus.enjoymusic.cache.PlayHistoryCache;
import com.zspirytus.enjoymusic.cache.constant.Constant;
import com.zspirytus.enjoymusic.engine.BackgroundMusicController;
import com.zspirytus.enjoymusic.engine.MusicPlayOrderManager;
import com.zspirytus.enjoymusic.entity.Music;
import com.zspirytus.enjoymusic.impl.binder.IBinderPoolImpl;
import com.zspirytus.enjoymusic.interfaces.IOnRemotePlayedListener;
import com.zspirytus.enjoymusic.receivers.MyHeadSetButtonClickBelowLReceiver;
import com.zspirytus.enjoymusic.receivers.MyHeadSetPlugOutReceiver;
import com.zspirytus.enjoymusic.services.media.MediaPlayController;
import com.zspirytus.enjoymusic.services.media.MyMediaSession;
import com.zspirytus.enjoymusic.utils.StatusBarUtil;
import com.zspirytus.enjoymusic.view.activity.MainActivity;

/**
 * Service: 负责播放、暂停音乐、发送Notification相关事件
 * Created by ZSpirytus on 2018/8/2.
 */

// TODO: 16/01/2019 onStartCommond接收到空intent
public class PlayMusicService extends BaseService implements IOnRemotePlayedListener {

    private static final String TAG = "PlayMusicService";

    private IBinderPoolImpl mBinderPool;

    private MyHeadSetPlugOutReceiver myHeadSetPlugOutReceiver;
    private MyHeadSetButtonClickBelowLReceiver myHeadSetButtonClickBelowLReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        MyMediaSession.getInstance().initMediaSession(this);
        MediaPlayController.getInstance().setOnPlayListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStatusBarEvent(intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinderPool == null)
            mBinderPool = new IBinderPoolImpl();
        return mBinderPool;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MediaPlayController.getInstance().timingToClearNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicSharedPreferences.saveMusic(CurrentPlayingMusicCache.getInstance().getCurrentPlayingMusic());
    }

    @Override
    protected void registerEvent() {
        myHeadSetPlugOutReceiver = new MyHeadSetPlugOutReceiver();
        IntentFilter headsetPlugOutFilter = new IntentFilter();
        headsetPlugOutFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(myHeadSetPlugOutReceiver, headsetPlugOutFilter);

        myHeadSetButtonClickBelowLReceiver = new MyHeadSetButtonClickBelowLReceiver();
        IntentFilter headsetButtonClickFilter = new IntentFilter();
        headsetButtonClickFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        registerReceiver(myHeadSetButtonClickBelowLReceiver, headsetButtonClickFilter);
    }

    @Override
    protected void unregisterEvent() {
        unregisterReceiver(myHeadSetPlugOutReceiver);
        unregisterReceiver(myHeadSetButtonClickBelowLReceiver);
    }

    @Override
    public void onPlay() {
        Notification currentNotification = NotificationHelper.getInstance().getCurrentNotification();
        int notificationNotifyId = NotificationHelper.getInstance().getNotificationNotifyId();
        /**
         * notificationNotifyId 不能为0
         * @see #startForeground(int, Notification)
         */
        startForeground(notificationNotifyId, currentNotification);
        MediaPlayController.getInstance().setOnPlayListener(null);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationHelper.getInstance().showNotification(CurrentPlayingMusicCache.getInstance().getCurrentPlayingMusic());
        Notification currentNotification = NotificationHelper.getInstance().getCurrentNotification();
        int notificationNotifyId = NotificationHelper.getInstance().getNotificationNotifyId();
        startForeground(notificationNotifyId, currentNotification);
    }

    private void handleStatusBarEvent(Intent intent) {
        if (intent != null) {
            String event = intent.getStringExtra(Constant.NotificationEvent.EXTRA);
            if (event != null) {
                switch (event) {
                    case Constant.NotificationEvent.SINGLE_CLICK:
                        startActivity();
                        StatusBarUtil.collapseStatusBar(this);
                        break;
                    case Constant.NotificationEvent.PREVIOUS:
                        BackgroundMusicController.getInstance().play(PlayHistoryCache.getInstance().getPreviousPlayedMusic());
                        break;
                    case Constant.NotificationEvent.PLAY:
                        BackgroundMusicController.getInstance().play(CurrentPlayingMusicCache.getInstance().getCurrentPlayingMusic());
                        break;
                    case Constant.NotificationEvent.PAUSE:
                        BackgroundMusicController.getInstance().pause();
                        break;
                    case Constant.NotificationEvent.NEXT:
                        BackgroundMusicController.getInstance().play(MusicPlayOrderManager.getInstance().getNextMusic(true));
                        break;
                }
            }
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        Music music = CurrentPlayingMusicCache.getInstance().getCurrentPlayingMusic();
        intent.putExtra(Constant.NotificationEvent.EXTRA, music);
        this.startActivity(intent);
    }
}
