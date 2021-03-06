package com.zspirytus.enjoymusic.cache.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.WorkerThread;

import com.zspirytus.basesdk.utils.LogUtil;
import com.zspirytus.basesdk.utils.ToastUtil;
import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.cache.ThreadPool;
import com.zspirytus.enjoymusic.db.QueryExecutor;
import com.zspirytus.enjoymusic.db.table.Album;
import com.zspirytus.enjoymusic.db.table.Artist;
import com.zspirytus.enjoymusic.db.table.ArtistArt;
import com.zspirytus.enjoymusic.db.table.Music;
import com.zspirytus.enjoymusic.engine.ForegroundMusicUpdator;
import com.zspirytus.enjoymusic.engine.MinEditDistance;
import com.zspirytus.enjoymusic.entity.listitem.MusicMetaDataListItem;
import com.zspirytus.enjoymusic.global.MainApplication;
import com.zspirytus.enjoymusic.online.RetrofitManager;
import com.zspirytus.enjoymusic.online.entity.OnlineAlbum;
import com.zspirytus.enjoymusic.online.entity.OnlineArtist;
import com.zspirytus.enjoymusic.online.entity.OnlineArtistList;
import com.zspirytus.enjoymusic.online.entity.response.SearchAlbumResponse;
import com.zspirytus.enjoymusic.online.entity.response.SearchArtistResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MusicMetaDataFragmentViewModel extends ViewModel {

    private static final String TAG = "MusicMetaDataFragmentVi";
    private MutableLiveData<List<MusicMetaDataListItem>> mMusicMetaList = new MutableLiveData<>();
    private List<MusicMetaDataListItem> dataList = new ArrayList<>();

    private MutableLiveData<Boolean> updateState = new MutableLiveData<>();
    private boolean hasUpdate = false;

    public MutableLiveData<List<MusicMetaDataListItem>> getMusicMetaList() {
        return mMusicMetaList;
    }

    public MutableLiveData<Boolean> getUpdateState() {
        return updateState;
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public void applyMusicMetaList(Music music) {
        ThreadPool.execute(() -> {
            Album album = QueryExecutor.findAlbum(music);
            Artist artist = QueryExecutor.findArtist(music);

            MusicMetaDataListItem item = new MusicMetaDataListItem();
            item.setArtistArt(true);
            item.setArtist(artist);
            dataList.add(item);

            MusicMetaDataListItem item1 = new MusicMetaDataListItem();
            item1.setMusic(true);
            item1.setMusic(music);
            item1.setAlbum(QueryExecutor.findAlbum(music));
            dataList.add(item1);

            /*MusicMetaDataListItem item2 = new MusicMetaDataListItem();
            item2.setTitle(true);
            item2.setTitle(MainApplication.getAppContext().getResources().getString(R.string.music_meta_data_download_music_info));
            dataList.add(item2);

            MusicMetaDataListItem item3 = new MusicMetaDataListItem();
            item3.setDownloadAlbumArtView(true);
            dataList.add(item3);*/

            MusicMetaDataListItem item4 = new MusicMetaDataListItem();
            item4.setTitle(true);
            item4.setTitle(MainApplication.getAppContext().getResources().getString(R.string.music_meta_data_edit_info));
            dataList.add(item4);

            MusicMetaDataListItem item5 = new MusicMetaDataListItem();
            item5.setSingleEditText(true);
            item5.setEditTextTitle(MainApplication.getAppContext().getResources().getString(R.string.music_meta_data_title));
            item5.setEditTextDefaultText(music.getMusicName());
            dataList.add(item5);

            MusicMetaDataListItem item6 = new MusicMetaDataListItem();
            item6.setSingleEditText(true);
            item6.setEditTextTitle(MainApplication.getAppContext().getResources().getString(R.string.music_meta_data_artist));
            item6.setEditTextDefaultText(artist.getArtistName());
            dataList.add(item6);

            MusicMetaDataListItem item7 = new MusicMetaDataListItem();
            item7.setSingleEditText(true);
            item7.setEditTextTitle(MainApplication.getAppContext().getResources().getString(R.string.music_meta_data_album));
            item7.setEditTextDefaultText(album.getAlbumName());
            dataList.add(item7);

            mMusicMetaList.postValue(dataList);
        });
    }

    public void updateMusic(List<MusicMetaDataListItem> datas, MainActivityViewModel viewModel) {
        updateState.setValue(false);
        ThreadPool.execute(() -> {
            // wrap need update data.
            Artist needUpdateArtist = datas.get(0).getArtist();
            Album needUpdateAlbum = datas.get(1).getAlbum();

            // update foreground & background data.
            if (needUpdateArtist.peakArtistArt() != null) {
                ForegroundMusicUpdator.getInstance().updateArtist(needUpdateArtist);
                viewModel.updateArtist(needUpdateArtist);
            }
            String albumArt = needUpdateAlbum.getAlbumArt();
            if (albumArt != null && (albumArt.contains("http") || albumArt.contains("https"))) {
                boolean isSuccess = ForegroundMusicUpdator.getInstance().updateAlbum(needUpdateAlbum);
                if (isSuccess) {
                    viewModel.updateAlbum(needUpdateAlbum);
                }
            }
            updateState.postValue(true);
        });
    }

    public void applyArtistArt(Music music) {
        Artist artist = QueryExecutor.findArtist(music);
        RetrofitManager.searchArtist(artist.getArtistName(), new Observer<SearchArtistResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SearchArtistResponse searchArtistResponse) {
                OnlineArtistList onlineArtistList = searchArtistResponse.getData();
                if (onlineArtistList == null) {
                    ToastUtil.postToShow(MainApplication.getAppContext(), R.string.download_failed);
                    return;
                }
                if (onlineArtistList.getArtistCount() == 0) {
                    ToastUtil.postToShow(MainApplication.getAppContext(), R.string.no_artist_art_available);
                    return;
                }
                List<OnlineArtist> onlineArtists = onlineArtistList.getArtists();
                double maxConfidence = 0;
                String picUrl = null;
                for (OnlineArtist onlineArtist : onlineArtists) {
                    if (onlineArtist.getName() != null) {
                        double confidence = MinEditDistance.SimilarDegree(onlineArtist.getName().toLowerCase(), artist.getArtistName().toLowerCase());
                        if (maxConfidence < confidence) {
                            maxConfidence = confidence;
                            picUrl = onlineArtist.getPicUrl();
                        }
                    }
                }
                updateArtistInfo(picUrl);
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.postToShow(MainApplication.getAppContext(), R.string.download_failed);
                LogUtil.log(MainApplication.getAppContext(), "download_artist.log", e);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public void applyAlbumArt(Music music) {
        Album album = QueryExecutor.findAlbum(music);
        RetrofitManager.searchAlbum(album.getAlbumName(), new Observer<SearchAlbumResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SearchAlbumResponse searchAlbumResponse) {
                List<OnlineAlbum> onlineAlbumList = searchAlbumResponse.getData();
                if (onlineAlbumList == null || onlineAlbumList.isEmpty()) {
                    ToastUtil.postToShow(MainApplication.getAppContext(), R.string.download_failed);
                    return;
                }
                double maxConfidence = 0;
                String picUrl = null;
                for (OnlineAlbum onlineAlbum : onlineAlbumList) {
                    double confidence = MinEditDistance.SimilarDegree(onlineAlbum.getAlbumName().toLowerCase(), album.getAlbumName().toLowerCase());
                    if (maxConfidence < confidence) {
                        maxConfidence = confidence;
                        picUrl = onlineAlbum.getAlbumPic();
                    }
                }
                updateAlbumArt(picUrl);
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.postToShow(MainApplication.getAppContext(), R.string.download_failed);
                LogUtil.log(MainApplication.getAppContext(), "download_album.log", e);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @WorkerThread
    private void updateArtistInfo(String picUrl) {
        if (picUrl != null) {
            hasUpdate = true;
            long artistId = dataList.get(0).getArtist().getArtistId();
            ArtistArt artistArt = new ArtistArt(artistId, picUrl);
            dataList.get(0).getArtist().setArtistArt(artistArt);
            mMusicMetaList.postValue(dataList);
        } else {
            ToastUtil.postToShow(MainApplication.getAppContext(), R.string.no_artist_art_available);
        }
    }

    @WorkerThread
    private void updateAlbumArt(String picUrl) {
        if (picUrl != null) {
            hasUpdate = true;
            Album album = dataList.get(1).getAlbum();
            album.setAlbumArt(picUrl);
            dataList.get(1).setAlbum(album);
        } else {
            ToastUtil.showToast(MainApplication.getAppContext(), R.string.no_artist_art_available);
        }
    }
}
