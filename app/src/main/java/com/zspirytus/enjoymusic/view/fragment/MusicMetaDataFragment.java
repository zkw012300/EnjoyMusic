package com.zspirytus.enjoymusic.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.adapter.MusicMetaDataListAdapter;
import com.zspirytus.enjoymusic.base.BaseFragment;
import com.zspirytus.enjoymusic.cache.viewmodels.MainActivityViewModel;
import com.zspirytus.enjoymusic.cache.viewmodels.MusicMetaDataFragmentViewModel;
import com.zspirytus.enjoymusic.db.table.Music;
import com.zspirytus.enjoymusic.engine.FragmentVisibilityManager;
import com.zspirytus.enjoymusic.factory.LayoutManagerFactory;
import com.zspirytus.enjoymusic.interfaces.annotations.LayoutIdInject;
import com.zspirytus.enjoymusic.interfaces.annotations.ViewInject;
import com.zspirytus.enjoymusic.view.dialog.SaveMusicInfoDialog;

@LayoutIdInject(R.layout.fragment_music_meta_data)
public class MusicMetaDataFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.cancel_btn)
    private ImageView mCancelBtn;
    @ViewInject(R.id.save_btn)
    private TextView mSaveBtn;

    private MusicMetaDataFragmentViewModel mViewModel;
    private MusicMetaDataListAdapter mAdapter;

    @Override
    protected void initData() {
        mViewModel = ViewModelProviders.of(this).get(MusicMetaDataFragmentViewModel.class);
        Music music = getArguments().getParcelable("music");
        mViewModel.obtainMusicMetaList(music);
        mAdapter = new MusicMetaDataListAdapter();
        mAdapter.setOnDownloadBtnClickListener(() -> {
            mViewModel.applyMusicData(getArguments().getParcelable("music"));
        });
    }

    @Override
    protected void initView() {
        mCancelBtn.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mRecyclerView.setLayoutManager(LayoutManagerFactory.createLinearLayoutManager(getContext()));
        fixNavBarHeight(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.getMusicMetaList().observe(this, values -> {
            mAdapter.setData(values);
        });
        // 这里注册一个ArtistList的观察者，以便在没有初始化ArtistListFragment的情况下，getValue()拿到非空值。
        ViewModelProviders.of(getParentActivity()).get(MainActivityViewModel.class)
                .getArtistList().observe(getParentActivity(), values -> {
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                MainActivityViewModel viewModel = ViewModelProviders.of(getParentActivity())
                        .get(MainActivityViewModel.class);
                mViewModel.updateMusic(mAdapter.getData(), viewModel);
                FragmentVisibilityManager.getInstance().remove(this);
                break;
            case R.id.cancel_btn:
                showDialog();
                break;
        }
    }

    @Override
    public int getContainerId() {
        return R.id.full_fragment_container;
    }

    @Override
    public int enterAnim() {
        return R.anim.anim_fragment_translate_show_up;
    }

    @Override
    public int exitAnim() {
        return super.exitAnim();
    }

    @Override
    public void goBack() {
        showDialog();
    }

    private void showDialog() {
        SaveMusicInfoDialog dialog = new SaveMusicInfoDialog();
        dialog.setOnDialogButtonClickListener(new SaveMusicInfoDialog.OnDialogButtonClickListener() {
            @Override
            public void onPositiveBtnClick() {
                FragmentVisibilityManager.getInstance().remove(MusicMetaDataFragment.this);
            }

            @Override
            public void onNegativeBtnClick() {
            }
        });
        dialog.show(getChildFragmentManager(), dialog.getClass().getSimpleName());
    }

    public static MusicMetaDataFragment getInstance(Music music) {
        MusicMetaDataFragment fragment = new MusicMetaDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("music", music);
        fragment.setArguments(bundle);
        return fragment;
    }
}
