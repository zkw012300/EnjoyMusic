package com.zspirytus.enjoymusic.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zspirytus.basesdk.recyclerview.adapter.SegmentLoadAdapter;
import com.zspirytus.basesdk.recyclerview.listeners.OnItemClickListener;
import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.adapter.MusicListAdapter;
import com.zspirytus.enjoymusic.base.LazyLoadBaseFragment;
import com.zspirytus.enjoymusic.cache.viewmodels.MainActivityViewModel;
import com.zspirytus.enjoymusic.db.table.Music;
import com.zspirytus.enjoymusic.engine.ForegroundMusicController;
import com.zspirytus.enjoymusic.engine.FragmentVisibilityManager;
import com.zspirytus.enjoymusic.entity.MusicFilter;
import com.zspirytus.enjoymusic.factory.FragmentFactory;
import com.zspirytus.enjoymusic.factory.LayoutManagerFactory;
import com.zspirytus.enjoymusic.interfaces.annotations.LayoutIdInject;
import com.zspirytus.enjoymusic.interfaces.annotations.ViewInject;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Fragment: 显示本地全部音乐列表
 * Created by ZSpirytus on 2018/8/2.
 */

@LayoutIdInject(R.layout.fragment_all_music_list_layout)
public class AllMusicListFragment extends LazyLoadBaseFragment
        implements OnItemClickListener {

    @ViewInject(R.id.all_music_recycler_view)
    private RecyclerView mMusicRecyclerView;
    @ViewInject(R.id.all_music_list_load_progress_bar)
    private ProgressBar mMusicListLoadProgressBar;
    @ViewInject(R.id.all_music_list_fragment_info_tv)
    private TextView mInfoTextView;

    private MusicListAdapter mAdapter;
    private AlphaInAnimationAdapter mAnimationWrapAdapter;

    @Override
    public void onItemClick(View view, int position) {
        Music music = mAdapter.getList().get(position);
        ForegroundMusicController.getInstance().play(music);
        ForegroundMusicController.getInstance().setPlayList(MusicFilter.NO_FILTER);
        FragmentVisibilityManager.getInstance().addCurrentFragmentToBackStack();
        FragmentVisibilityManager.getInstance().show(FragmentFactory.getInstance().get(MusicPlayFragment.class));
    }

    @Override
    protected void initData() {
        mAdapter = new MusicListAdapter();
        mAdapter.setOnItemClickListener(this);
        mAnimationWrapAdapter = new AlphaInAnimationAdapter(new SegmentLoadAdapter(mAdapter));
        mAnimationWrapAdapter.setDuration(618);
        mAnimationWrapAdapter.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void initView() {
        mMusicRecyclerView.setVisibility(View.GONE);
        mMusicListLoadProgressBar.setVisibility(View.VISIBLE);
        mMusicRecyclerView.setLayoutManager(LayoutManagerFactory.createLinearLayoutManager(getParentActivity()));
        mMusicRecyclerView.setHasFixedSize(true);
        mMusicRecyclerView.setNestedScrollingEnabled(false);
        mMusicRecyclerView.setAdapter(mAnimationWrapAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(getParentActivity())
                .get(MainActivityViewModel.class)
                .getMusicList().observe(getParentActivity(), (values) -> {
            mMusicListLoadProgressBar.setVisibility(View.GONE);
            if (values != null && !values.isEmpty()) {
                mAdapter.setList(values);
                mAnimationWrapAdapter.notifyDataSetChanged();
                mMusicRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mInfoTextView.setVisibility(View.VISIBLE);
                mInfoTextView.setText(R.string.no_music_tip);
            }
        });
    }

    @Override
    public int getContainerId() {
        return 0;
    }

    public static AllMusicListFragment getInstance() {
        AllMusicListFragment allMusicListFragment = new AllMusicListFragment();
        return allMusicListFragment;
    }

}
