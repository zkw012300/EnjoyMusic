package com.zspirytus.enjoymusic.view.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zspirytus.enjoymusic.Interface.ViewInject;
import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.model.Music;
import com.zspirytus.enjoymusic.services.MediaPlayHelper;
import com.zspirytus.enjoymusic.services.MusicPlayingObserver;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;

/**
 * Created by ZSpirytus on 2018/8/2.
 */

public class MusicPlayFragment extends BaseFragment
        implements View.OnClickListener,MusicPlayingObserver {

    private static final String MUSIC_KEY = "music_key";
    private static MediaPlayHelper mediaPlayHelper = MediaPlayHelper.getInstance();

    @ViewInject(R.id.title)
    private TextView mTitle;

    @ViewInject(R.id.cover)
    private ImageView mCover;

    @ViewInject(R.id.now_time)
    private TextView mNowTime;
    @ViewInject(R.id.music_seekbar)
    private SeekBar mSeekBar;
    @ViewInject(R.id.total_time)
    private TextView mTotalTime;

    @ViewInject(R.id.previous)
    private ImageView mPreviousButton;
    @ViewInject(R.id.play_pause)
    private ImageView mPlayOrPauseButton;
    @ViewInject(R.id.next)
    private ImageView mNextButton;

    private Music currentPlayingMusic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        registerEvent();
        initView();
        setListener();
        return view;
    }

    @Override
    public void onDestroyView() {
        unRegisterEvent();
        super.onDestroyView();
    }

    @Override
    public Integer getLayoutId() {
        return R.layout.fragment_music_play;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cover:
                break;
            case R.id.previous:
                break;
            case R.id.play_pause:
                boolean isPlaying = MediaPlayHelper.isPlaying();
                if (isPlaying) {
                    EventBus.getDefault().post(currentPlayingMusic,"pause");
                } else {
                    EventBus.getDefault().post(currentPlayingMusic,"play");
                }
                break;
            case R.id.next:
                break;
        }
    }

    @Override
    public void update(boolean isPlaying) {
        setButtonSrc(isPlaying);
    }

    private void initView() {
        Music music = (Music) getArguments().getSerializable(MUSIC_KEY);
        if (music != null) {
            mTitle.setText(music.getmMusicName());
            Glide.with(this).load(new File(music.getmMusicThumbAlbumUri()))
                    .into(mCover);
            mTotalTime.setText(music.getDuration());
            currentPlayingMusic = music;
        }
        setButtonSrc(MediaPlayHelper.isPlaying());
    }

    private void setButtonSrc(boolean isPlaying) {
        if (isPlaying) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mPlayOrPauseButton,"alpha",1f,0f);
            animator.setDuration(382);
            animator.start();
            Glide.with(this).load(R.drawable.ic_pause_thin).into(mPlayOrPauseButton);
            animator = ObjectAnimator.ofFloat(mPlayOrPauseButton,"alpha",0f,1f);
            animator.setDuration(382);
            animator.start();
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mPlayOrPauseButton,"alpha",1f,0f);
            animator.setDuration(382);
            animator.start();
            Glide.with(this).load(R.drawable.ic_play_thin).into(mPlayOrPauseButton);
            animator = ObjectAnimator.ofFloat(mPlayOrPauseButton,"alpha",0f,1f);
            animator.setDuration(382);
            animator.start();
        }
    }

    private void setListener() {
        mCover.setOnClickListener(this);
        mPreviousButton.setOnClickListener(this);
        mPlayOrPauseButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
    }

    private void registerEvent() {
        EventBus.getDefault().register(this);
        MediaPlayHelper.register(this);
    }

    private void unRegisterEvent() {
        EventBus.getDefault().unregister(this);
        MediaPlayHelper.unregister(this);
    }

    @Subscriber(tag = "music_name_set")
    public void setView(Music music) {
        mTitle.setText(music.getmMusicName());
        Glide.with(this).load(new File(music.getmMusicThumbAlbumUri())).into(mCover);
        mTotalTime.setText(music.getDuration());
        currentPlayingMusic = music;
    }

    public static MusicPlayFragment getInstance(Music music) {
        MusicPlayFragment musicPlayFragment = new MusicPlayFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(MUSIC_KEY, music);
        musicPlayFragment.setArguments(bundle);
        return musicPlayFragment;
    }

}
