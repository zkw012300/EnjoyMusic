package com.zspirytus.enjoymusic.view.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.adapter.CommonItemRecyclerViewAdapter;
import com.zspirytus.enjoymusic.cache.ForegroundMusicCache;
import com.zspirytus.enjoymusic.cache.constant.Constant;
import com.zspirytus.enjoymusic.engine.ForegroundMusicController;
import com.zspirytus.enjoymusic.entity.Artist;
import com.zspirytus.enjoymusic.entity.Music;
import com.zspirytus.enjoymusic.entity.MusicFilter;
import com.zspirytus.enjoymusic.factory.LayoutManagerFactory;
import com.zspirytus.enjoymusic.interfaces.annotations.LayoutIdInject;
import com.zspirytus.enjoymusic.interfaces.annotations.ViewInject;
import com.zspirytus.enjoymusic.listeners.OnRecyclerViewItemClickListener;
import com.zspirytus.enjoymusic.utils.AnimationUtil;

import java.util.List;

/**
 * Fragment 以艺术家名筛选的音乐列表
 * Created by ZSpirytus on 2018/9/12.
 */
// TODO: 2018/9/17 click recyclerview to navigate to corresponding music list
@LayoutIdInject(R.layout.fragment_artist_music_list)
public class ArtistMusicListFragment extends LazyLoadBaseFragment
        implements OnRecyclerViewItemClickListener {

    @ViewInject(R.id.artist_music_recycler_view)
    private RecyclerView mArtistMusicRecyclerView;
    @ViewInject(R.id.artist_music_list_load_progress_bar)
    private ProgressBar mLoadProgressBar;
    @ViewInject(R.id.artist_music_list_fragment_info_tv)
    private TextView mInfoTextView;

    private List<Artist> mArtistList;

    private CommonItemRecyclerViewAdapter<Artist> mAdapter;

    @Override
    public void onItemClick(View view, int position) {
        String artistName = mArtistList.get(position).getArtistName();
        Music targetArtistFirstMusic = null;
        for (Music music : ForegroundMusicCache.getInstance().getAllMusicList()) {
            if (artistName.equals(music.getMusicArtist())) {
                targetArtistFirstMusic = music;
                break;
            }
        }
        ForegroundMusicController.getInstance().play(targetArtistFirstMusic);
        ForegroundMusicController.getInstance().setPlayList(new MusicFilter(null, artistName));
    }

    @Override
    protected void initData() {
        mArtistList = ForegroundMusicCache.getInstance().getArtistList();
    }

    @Override
    protected void initView() {
        if (mArtistList != null && !mArtistList.isEmpty()) {
            mAdapter = new CommonItemRecyclerViewAdapter<>(mArtistList);
            mArtistMusicRecyclerView.setLayoutManager(LayoutManagerFactory.createLinearLayoutManager(getParentActivity()));
            mArtistMusicRecyclerView.setHasFixedSize(true);
            mArtistMusicRecyclerView.setNestedScrollingEnabled(false);
            mAdapter.setOnItemClickListener(ArtistMusicListFragment.this);
            mArtistMusicRecyclerView.setAdapter(mAdapter);
            playWidgetAnimation(true, false);
        } else {
            playWidgetAnimation(true, true);
        }
    }

    private void playWidgetAnimation(boolean isSuccess, boolean isEmpty) {
        AnimationUtil.ofFloat(mLoadProgressBar, Constant.AnimationProperty.ALPHA, 1f, 0f).start();
        mLoadProgressBar.setVisibility(View.GONE);
        if (isSuccess) {
            if (!isEmpty) {
                AnimationUtil.ofFloat(mArtistMusicRecyclerView, Constant.AnimationProperty.ALPHA, 0f, 1f).start();
            } else {
                AnimationUtil.ofFloat(mInfoTextView, Constant.AnimationProperty.ALPHA, 0f, 1f).start();
                mInfoTextView.setVisibility(View.VISIBLE);
            }
        } else {
            AnimationUtil.ofFloat(mInfoTextView, Constant.AnimationProperty.ALPHA, 0f, 1f).start();
            mInfoTextView.setVisibility(View.VISIBLE);
        }
    }

    public static ArtistMusicListFragment getInstance() {
        ArtistMusicListFragment instance = new ArtistMusicListFragment();
        return instance;
    }
}