package com.zspirytus.enjoymusic.impl.binder;

import com.zspirytus.enjoymusic.ISetPlayList;
import com.zspirytus.enjoymusic.engine.MusicPlayOrderManager;
import com.zspirytus.enjoymusic.entity.MusicFilter;

public class PlayListSetter extends ISetPlayList.Stub {

    private static class SingletonHolder {
        static PlayListSetter INSTANCE = new PlayListSetter();
    }

    private PlayListSetter() {
    }

    public static PlayListSetter getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void setPlayList(MusicFilter musicFilter) {
        MusicPlayOrderManager.getInstance().setPlayList(musicFilter);
    }
}