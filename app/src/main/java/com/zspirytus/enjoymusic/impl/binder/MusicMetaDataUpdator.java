package com.zspirytus.enjoymusic.impl.binder;

import android.os.RemoteException;

import com.zspirytus.enjoymusic.IMusicMetaDataUpdator;
import com.zspirytus.enjoymusic.db.DBManager;
import com.zspirytus.enjoymusic.db.table.Album;
import com.zspirytus.enjoymusic.db.table.Artist;
import com.zspirytus.enjoymusic.db.table.ArtistArt;
import com.zspirytus.enjoymusic.global.MainApplication;
import com.zspirytus.enjoymusic.impl.glide.GlideApp;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class MusicMetaDataUpdator extends IMusicMetaDataUpdator.Stub {

    private static class Singleton {
        static MusicMetaDataUpdator INSTANCE = new MusicMetaDataUpdator();
    }

    private MusicMetaDataUpdator() {
    }

    public static MusicMetaDataUpdator getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public void updateArtist(Artist artist) {
        ArtistArt artistArt = artist.peakArtistArt();
        DBManager.getInstance().getDaoSession().getArtistArtDao().insertOrReplace(artistArt);
    }

    @Override
    public void updateAlbum(Album album) throws RemoteException {
        String picUrl = album.getCustomAlbumArt();
        try {
            File file = GlideApp.with(MainApplication.getBackgroundContext()).asFile().load(picUrl).submit().get();
            album.setCustomAlbumArt(file.getAbsolutePath());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        DBManager.getInstance().getDaoSession().getAlbumDao().insertOrReplace(album);
    }
}
