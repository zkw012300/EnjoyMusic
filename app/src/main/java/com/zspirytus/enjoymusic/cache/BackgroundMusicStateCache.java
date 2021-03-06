package com.zspirytus.enjoymusic.cache;

import com.zspirytus.enjoymusic.db.QueryExecutor;
import com.zspirytus.enjoymusic.db.table.Music;

/**
 * Created by ZSpirytus on 2018/9/12.
 */

public class BackgroundMusicStateCache {

    private static final BackgroundMusicStateCache ourInstance = new BackgroundMusicStateCache();

    private Music currentPlayingMusic;
    private boolean isPlaying;

    public static BackgroundMusicStateCache getInstance() {
        return ourInstance;
    }

    private BackgroundMusicStateCache() {
    }

    public void init() {
        currentPlayingMusic = QueryExecutor.getLastestPlayMusic();
    }

    public void setCurrentPlayingMusic(Music music) {
        currentPlayingMusic = music;
    }

    public Music getCurrentPlayingMusic() {
        return currentPlayingMusic;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
