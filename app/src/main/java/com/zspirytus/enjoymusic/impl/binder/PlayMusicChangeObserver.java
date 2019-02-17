package com.zspirytus.enjoymusic.impl.binder;

import com.zspirytus.enjoymusic.entity.Music;
import com.zspirytus.enjoymusic.foregroundobserver.IPlayedMusicChangeObserver;
import com.zspirytus.enjoymusic.receivers.observer.PlayedMusicChangeObserver;

import java.util.ArrayList;
import java.util.List;

public class PlayMusicChangeObserver extends IPlayedMusicChangeObserver.Stub {

    private Music mEvent;

    private static class SingletonHolder {
        static PlayMusicChangeObserver INSTANCE = new PlayMusicChangeObserver();
    }

    private List<PlayedMusicChangeObserver> observers = new ArrayList<>();

    private PlayMusicChangeObserver() {
    }

    public static PlayMusicChangeObserver getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void onPlayMusicChange(Music currentPlayingMusic) {
        mEvent = currentPlayingMusic;
        for (PlayedMusicChangeObserver observer : observers) {
            observer.onPlayedMusicChanged(currentPlayingMusic);
        }
    }

    public void register(PlayedMusicChangeObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            if (mEvent != null)
                observer.onPlayedMusicChanged(mEvent);
        }
    }

    public void unregister(PlayedMusicChangeObserver observer) {
        observers.remove(observer);
    }
}