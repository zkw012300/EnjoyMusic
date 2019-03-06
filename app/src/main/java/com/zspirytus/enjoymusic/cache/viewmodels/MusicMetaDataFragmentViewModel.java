package com.zspirytus.enjoymusic.cache.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.WorkerThread;

import com.zspirytus.enjoymusic.IMusicMetaDataUpdator;
import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.cache.ThreadPool;
import com.zspirytus.enjoymusic.cache.constant.Constant;
import com.zspirytus.enjoymusic.db.QueryExecutor;
import com.zspirytus.enjoymusic.db.table.Album;
import com.zspirytus.enjoymusic.db.table.Artist;
import com.zspirytus.enjoymusic.db.table.ArtistArt;
import com.zspirytus.enjoymusic.db.table.Music;
import com.zspirytus.enjoymusic.engine.ForegroundBinderManager;
import com.zspirytus.enjoymusic.engine.MinEditDistance;
import com.zspirytus.enjoymusic.entity.listitem.MusicMetaDataListItem;
import com.zspirytus.enjoymusic.global.MainApplication;
import com.zspirytus.enjoymusic.online.RetrofitManager;
import com.zspirytus.enjoymusic.online.entity.OnlineArtist;
import com.zspirytus.enjoymusic.online.entity.OnlineArtistList;
import com.zspirytus.enjoymusic.online.entity.response.SearchArtistResponse;
import com.zspirytus.enjoymusic.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MusicMetaDataFragmentViewModel extends ViewModel {

    private static final String TAG = "MusicMetaDataFragmentVi";
    private MutableLiveData<List<MusicMetaDataListItem>> mMusicMetaList = new MutableLiveData<>();
    private List<MusicMetaDataListItem> dataList = new ArrayList<>();

    private MutableLiveData<Boolean> updateState = new MutableLiveData<>();

    public MutableLiveData<List<MusicMetaDataListItem>> getMusicMetaList() {
        return mMusicMetaList;
    }

    public MutableLiveData<Boolean> getUpdateState() {
        return updateState;
    }

    public void obtainMusicMetaList(Music music) {
        Album album = QueryExecutor.findAlbum(music);
        Artist artist = QueryExecutor.findArtist(music);

        MusicMetaDataListItem item = new MusicMetaDataListItem();
        item.setArtistArt(true);
        item.setArtist(artist);
        dataList.add(item);

        MusicMetaDataListItem item1 = new MusicMetaDataListItem();
        item1.setMusic(true);
        item1.setMusic(music);
        dataList.add(item1);

        MusicMetaDataListItem item2 = new MusicMetaDataListItem();
        item2.setTitle(true);
        item2.setTitle(MainApplication.getForegroundContext().getResources().getString(R.string.music_meta_data_download_music_info));
        dataList.add(item2);

        MusicMetaDataListItem item3 = new MusicMetaDataListItem();
        item3.setDownloadAlbumArtView(true);
        dataList.add(item3);

        MusicMetaDataListItem item4 = new MusicMetaDataListItem();
        item4.setTitle(true);
        item4.setTitle(MainApplication.getForegroundContext().getResources().getString(R.string.music_meta_data_edit_info));
        dataList.add(item4);

        MusicMetaDataListItem item5 = new MusicMetaDataListItem();
        item5.setSingleEditText(true);
        item5.setEditTextTitle(MainApplication.getForegroundContext().getResources().getString(R.string.music_meta_data_title));
        item5.setEditTextDefaultText(music.getMusicName());
        dataList.add(item5);

        MusicMetaDataListItem item6 = new MusicMetaDataListItem();
        item6.setSingleEditText(true);
        item6.setEditTextTitle(MainApplication.getForegroundContext().getResources().getString(R.string.music_meta_data_artist));
        item6.setEditTextDefaultText(artist.getArtistName());
        dataList.add(item6);

        MusicMetaDataListItem item7 = new MusicMetaDataListItem();
        item7.setSingleEditText(true);
        item7.setEditTextTitle(MainApplication.getForegroundContext().getResources().getString(R.string.music_meta_data_album));
        item7.setEditTextDefaultText(album.getAlbumName());
        dataList.add(item7);

        mMusicMetaList.setValue(dataList);
    }

    public void updateMusic(List<MusicMetaDataListItem> datas, MainActivityViewModel viewModel) {
        updateState.setValue(false);
        ThreadPool.execute(() -> {
            // wrap need update data.
            Artist needUpdateArtist = datas.get(0).getArtist();

            // update foreground data.
            List<Artist> artistList = viewModel.getArtistList().getValue();
            for (int i = 0; i < artistList.size(); i++) {
                if (needUpdateArtist.getArtistId().equals(artistList.get(i).getArtistId())) {
                    artistList.get(i).setArtistArt(needUpdateArtist.peakArtistArt());
                    break;
                }
            }
            viewModel.getArtistList().postValue(artistList);

            // update background data.
            IBinder binder = ForegroundBinderManager.getInstance().getBinderByBinderCode(Constant.BinderCode.MUSIC_META_DATA_UPDATOR);
            IMusicMetaDataUpdator updator = IMusicMetaDataUpdator.Stub.asInterface(binder);
            // TODO: 2019/3/6 check null.
            // TODO: 2019/3/6 update if data has change.
            try {
                updator.updateArtist(needUpdateArtist);
            } catch (RemoteException e) {
                e.printStackTrace();
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
                    AndroidSchedulers.mainThread().scheduleDirect(() -> ToastUtil.showToast(MainApplication.getForegroundContext(), R.string.download_failed));
                    return;
                }
                if (onlineArtistList.getArtistCount() == 0) {
                    AndroidSchedulers.mainThread().scheduleDirect(() -> ToastUtil.showToast(MainApplication.getForegroundContext(), R.string.no_artist_art_available));
                    return;
                }
                List<OnlineArtist> onlineArtists = onlineArtistList.getArtists();
                double maxConfidence = 0;
                String picUrl = null;
                for (OnlineArtist onlineArtist : onlineArtists) {
                    if (onlineArtist.getName() != null) {
                        double confidence = MinEditDistance.SimilarDegree(onlineArtist.getName(), artist.getArtistName());
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
                ToastUtil.showToast(MainApplication.getForegroundContext(), R.string.download_failed);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @WorkerThread
    private void updateArtistInfo(String picUrl) {
        if (picUrl != null) {
            long artistId = dataList.get(0).getArtist().getArtistId();
            ArtistArt artistArt = new ArtistArt(artistId, picUrl);
            dataList.get(0).getArtist().setArtistArt(artistArt);
            mMusicMetaList.postValue(dataList);
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(() -> ToastUtil.showToast(MainApplication.getForegroundContext(), R.string.no_artist_art_available));
        }
    }
}
