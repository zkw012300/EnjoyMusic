package com.zspirytus.enjoymusic.factory;

import com.zspirytus.enjoymusic.entity.Music;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ZSpirytus on 2018/9/17.
 */

public class ObservableFactory {

    /*public static Observable<Object> getMusicListInForegroundObservable() {
        return Observable.create((emitter) -> {
            IBinder binder = ForegroundBinderManager.getInstance().getBinderByBinderCode(Constant.BinderCode.GET_MUSIC_LIST);
            IGetMusicList getMusicListBinder = IGetMusicList.Stub.asInterface(binder);
            ForegroundMusicStateCache.getInstance().setAllMusicList(getMusicListBinder.getMusicList());
            ForegroundMusicStateCache.getInstance().setAlbumList(getMusicListBinder.getAlbumList());
            ForegroundMusicStateCache.getInstance().setArtistList(getMusicListBinder.getArtistList());
            ForegroundMusicStateCache.getInstance().setFolderSortedMusicList(getMusicListBinder.getFolderSortedMusic());
            IBinder iBinder = ForegroundBinderManager.getInstance().getBinderByBinderCode(Constant.BinderCode.SET_PLAY_LIST);
            ISetPlayList setPlayList = ISetPlayList.Stub.asInterface(iBinder);
            try {
                setPlayList.setPlayList(MusicFilter.NO_FILTER);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            emitter.onNext(1);
            emitter.onComplete();
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }*/

    public static Single<List<Music>> filterMusic(List<Music> musicList, final String filterAlbum, final String filterArtist) {
        return Observable.fromIterable(musicList)
                .filter((music -> {
                    String album = music.getMusicAlbumName();
                    String artist = music.getMusicArtist();
                    return (album != null && album.equals(filterAlbum))
                            || (artist != null && artist.equals(filterArtist));
                })).toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

}
