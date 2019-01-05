package com.zspirytus.enjoymusic.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.cache.MusicCoverFileCache;
import com.zspirytus.enjoymusic.entity.Music;
import com.zspirytus.enjoymusic.impl.glide.GlideApp;

public class MusicControlPane extends LinearLayout implements View.OnClickListener {

    private OnViewClickListener mListener;

    public MusicControlPane(Context context) {
        this(context, null);
    }

    public MusicControlPane(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicControlPane(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bottom_music_control, this);
        findViewById(R.id.bottom_music_play_pause).setOnClickListener(this);
        findViewById(R.id.bottom_music_next).setOnClickListener(this);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_music_play_pause:
                mListener.onPlayOrPause();
                break;
            case R.id.bottom_music_next:
                mListener.onNext();
                break;
            case R.id.bottom_music_control:
                mListener.onClick();
                break;
        }
    }

    public void setOnViewClickListener(OnViewClickListener listener) {
        mListener = listener;
    }

    public void wrapMusic(Music music) {
        String path = music.getMusicThumbAlbumCoverPath();
        if (path != null && !path.isEmpty()) {
            GlideApp.with(this)
                    .load(MusicCoverFileCache.getInstance().getCoverFile(path))
                    .into((ImageView) findViewById(R.id.bottom_music_cover));
        } else {
            GlideApp.with(this)
                    .load(R.drawable.defalut_cover)
                    .into((ImageView) findViewById(R.id.bottom_music_cover));
        }
        ((TextView) findViewById(R.id.bottom_music_name)).setText(music.getMusicName());
        ((TextView) findViewById(R.id.bottom_music_album)).setText(music.getMusicAlbumName());
    }

    public void setPlayState(boolean isPlaying) {
        int resId = isPlaying ? R.drawable.ic_pause_black_48dp : R.drawable.ic_play_arrow_black_48dp;
        ((ImageView) findViewById(R.id.bottom_music_play_pause)).setImageResource(resId);
    }

    public interface OnViewClickListener {

        void onPlayOrPause();

        void onNext();

        void onClick();
    }
}
