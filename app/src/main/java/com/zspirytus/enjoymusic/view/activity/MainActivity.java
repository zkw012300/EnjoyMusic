package com.zspirytus.enjoymusic.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;

import com.zspirytus.enjoymusic.AndroidBug5497Workaround;
import com.zspirytus.enjoymusic.IBinderPool;
import com.zspirytus.enjoymusic.IGetMusicList;
import com.zspirytus.enjoymusic.ISetPlayList;
import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.base.BaseActivity;
import com.zspirytus.enjoymusic.base.BaseFragment;
import com.zspirytus.enjoymusic.cache.constant.Constant;
import com.zspirytus.enjoymusic.cache.viewmodels.MainActivityViewModel;
import com.zspirytus.enjoymusic.engine.ForegroundBinderManager;
import com.zspirytus.enjoymusic.engine.ForegroundMusicController;
import com.zspirytus.enjoymusic.engine.FragmentVisibilityManager;
import com.zspirytus.enjoymusic.entity.Album;
import com.zspirytus.enjoymusic.entity.Artist;
import com.zspirytus.enjoymusic.entity.FolderSortedMusic;
import com.zspirytus.enjoymusic.entity.Music;
import com.zspirytus.enjoymusic.entity.MusicFilter;
import com.zspirytus.enjoymusic.factory.FragmentFactory;
import com.zspirytus.enjoymusic.impl.DrawerListenerImpl;
import com.zspirytus.enjoymusic.impl.binder.IPlayMusicChangeObserverImpl;
import com.zspirytus.enjoymusic.impl.binder.IPlayStateChangeObserverImpl;
import com.zspirytus.enjoymusic.interfaces.annotations.LayoutIdInject;
import com.zspirytus.enjoymusic.interfaces.annotations.ViewInject;
import com.zspirytus.enjoymusic.receivers.observer.MusicPlayStateObserver;
import com.zspirytus.enjoymusic.receivers.observer.PlayedMusicChangeObserver;
import com.zspirytus.enjoymusic.services.PlayMusicService;
import com.zspirytus.enjoymusic.view.fragment.HomePageFragment;
import com.zspirytus.enjoymusic.view.fragment.LaunchAnimationFragment;
import com.zspirytus.enjoymusic.view.fragment.MusicListDetailFragment;
import com.zspirytus.enjoymusic.view.fragment.MusicPlayFragment;
import com.zspirytus.enjoymusic.view.widget.CustomNavigationView;
import com.zspirytus.enjoymusic.view.widget.MusicControlPane;
import com.zspirytus.zspermission.PermissionGroup;
import com.zspirytus.zspermission.ZSPermission;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Activity: 控制显示、隐藏Fragment.
 * Created by ZSpirytus on 2018/8/2.
 */

@LayoutIdInject(R.layout.activity_main)
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, PlayedMusicChangeObserver,
        MusicPlayStateObserver {

    @ViewInject(R.id.main_drawer)
    private DrawerLayout mDrawerLayout;
    @ViewInject(R.id.nav_view)
    private CustomNavigationView mCustomNavigationView;

    @ViewInject(R.id.bottom_music_control)
    private MusicControlPane mBottomMusicControl;

    private DrawerListenerImpl mDrawerListener;

    private MainActivityViewModel mViewModel;
    private ServiceConnection conn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidBug5497Workaround.assistActivity(this);
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        bindPlayMusicService();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentVisibilityManager.getInstance().onSaveInstanceState(outState);
    }

    @Override
    protected void onMRestoreInstanceState(Bundle savedInstanceState) {
        e("MainActivity#onRestoreInstanceState");
        setLightStatusIconColor();
        FragmentVisibilityManager.getInstance().init(getSupportFragmentManager());
        FragmentVisibilityManager.getInstance().onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            BaseFragment currentFragment = FragmentVisibilityManager.getInstance().getCurrentFragment();
            currentFragment.goBack();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Music action = intent.getParcelableExtra(Constant.NotificationEvent.EXTRA);
        if (action != null) {
            if (!FragmentVisibilityManager.getInstance().getCurrentFragment().getClass().getSimpleName().equals("MusicPlayingFragment")) {
                MusicPlayFragment fragment = FragmentFactory.getInstance().get(MusicPlayFragment.class);
                mViewModel.setCurrentPlayingMusic(action);
                showCastFragment(fragment);
            }
        }
    }

    @SuppressWarnings("all")
    @Override
    protected void initView() {
        mDrawerListener = new DrawerListenerImpl();
        mDrawerLayout.addDrawerListener(mDrawerListener);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mBottomMusicControl.setOnViewClickListener(new MusicControlPane.OnViewClickListener() {
            @Override
            public void onPlayOrPause() {
                if (mViewModel.getMusicPlayState().getValue()) {
                    ForegroundMusicController.getInstance().pause();
                } else {
                    ForegroundMusicController.getInstance().play(mViewModel.getCurrentPlayingMusic().getValue());
                }
            }

            @Override
            public void onNext() {
                ForegroundMusicController.getInstance().playNext(true);
            }

            @Override
            public void onClick() {
                showCastFragment(FragmentFactory.getInstance().get(MusicPlayFragment.class));
            }
        });
        mCustomNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        e("MainActivity#initData");
        FragmentVisibilityManager.getInstance().init(getSupportFragmentManager());
        Music music = getIntent().getParcelableExtra("music");
        if (music != null) {
            mViewModel.setCurrentPlayingMusic(music);
            MusicPlayFragment fragment = FragmentFactory.getInstance().get(MusicPlayFragment.class);
            showCastFragment(fragment);
            BaseFragment homeFragment = FragmentFactory.getInstance().get(HomePageFragment.class);
            FragmentVisibilityManager.getInstance().addToBackStack(homeFragment);
        } else {
            showCastFragment(FragmentFactory.getInstance().get(HomePageFragment.class));
        }
    }

    @Override
    protected void registerEvent() {
        EventBus.getDefault().register(this);
        IPlayMusicChangeObserverImpl.getInstance().register(this);
        IPlayStateChangeObserverImpl.getInstance().register(this);
    }

    @Override
    protected void unregisterEvent() {
        EventBus.getDefault().unregister(this);
        ForegroundMusicController.getInstance().release();
        mCustomNavigationView.unregisterFragmentChangeListener();
        IPlayMusicChangeObserverImpl.getInstance().unregister(this);
        IPlayStateChangeObserverImpl.getInstance().unregister(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // navigate to selected fragment when mDrawerLayout close completed
        // closing state: begin closing, closing finish closing will listen by DrawerListenerImpl
        mDrawerListener.setSelectedNavId(item.getItemId());
        mDrawerLayout.closeDrawer(Gravity.START);
        return true;
    }

    @Override
    public void onGranted() {
        loadMusicList();
    }

    @Override
    public void onPlayedMusicChanged(final Music music) {
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            if (music != null) {
                mViewModel.setCurrentPlayingMusic(music);
                mBottomMusicControl.wrapMusic(music);
            }
        });
    }

    @Override
    public void onPlayingStateChanged(final boolean isPlaying) {
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            mViewModel.setMusicPlayState(isPlaying);
            mBottomMusicControl.setPlayState(isPlaying);
        });
    }

    @Subscriber(tag = Constant.EventBusTag.SHOW_CAST_FRAGMENT)
    public <T extends BaseFragment> void showCastFragment(T shouldShowFragment) {
        BaseFragment currentFragment = FragmentVisibilityManager.getInstance().getCurrentFragment();
        if (shouldShowFragment instanceof MusicPlayFragment || shouldShowFragment instanceof MusicListDetailFragment) {
            if (currentFragment != null) {
                FragmentVisibilityManager.getInstance().addToBackStack(currentFragment);
            }
        }
        FragmentVisibilityManager.getInstance().show(shouldShowFragment);
    }

    @Subscriber(tag = Constant.EventBusTag.OPEN_DRAWER)
    public void setDrawerOpenOrNot(boolean isOpen) {
        if (isOpen)
            mDrawerLayout.openDrawer(Gravity.START);
    }

    private void loadMusicList() {
        Schedulers.io().scheduleDirect(() -> {
            try {
                IBinder binder = ForegroundBinderManager.getInstance().getBinderByBinderCode(Constant.BinderCode.GET_MUSIC_LIST);
                IGetMusicList getMusicListBinder = IGetMusicList.Stub.asInterface(binder);
                final List<Music> musicList = getMusicListBinder.getMusicList();
                final List<Album> albumList = getMusicListBinder.getAlbumList();
                final List<Artist> artistList = getMusicListBinder.getArtistList();
                final List<FolderSortedMusic> folderSortedMusicList = getMusicListBinder.getFolderSortedMusic();
                IBinder iBinder = ForegroundBinderManager.getInstance().getBinderByBinderCode(Constant.BinderCode.SET_PLAY_LIST);
                ISetPlayList setPlayList = ISetPlayList.Stub.asInterface(iBinder);
                setPlayList.setPlayList(MusicFilter.NO_FILTER);
                AndroidSchedulers.mainThread().scheduleDirect(() -> {
                    mViewModel.setMusicList(musicList);
                    mViewModel.setAlbumList(albumList);
                    mViewModel.setArtistList(artistList);
                    mViewModel.setFolderList(folderSortedMusicList);
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    private void requestPermission() {
        // request permissions, if it is granted, it will call onGranted()
        ZSPermission.getInstance()
                .at(this)
                .requestCode(123)
                .permissions(PermissionGroup.STORAGE_GROUP)
                .permissions(PermissionGroup.PHONE_GROUP)
                .listenBy(this)
                .request();
    }

    private void bindPlayMusicService() {
        Intent startPlayMusicServiceIntent = new Intent(this, PlayMusicService.class);
        /**
         * 先启动Service，走onStartCommand并返回START_STICKY.
         * 这样Service就一直是粘性的.
         */
        startService(startPlayMusicServiceIntent);
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ForegroundBinderManager.getInstance().init(IBinderPool.Stub.asInterface(service));
                requestPermission();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ForegroundBinderManager.getInstance().release();
            }
        };
        bindService(startPlayMusicServiceIntent, conn, BIND_AUTO_CREATE);
    }

    private void showLaunchAnimation() {
        setFullScreenOrNot(true);
        final LaunchAnimationFragment fragment = new LaunchAnimationFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.launch_animation_container, fragment)
                .show(fragment)
                .commitAllowingStateLoss();
        AndroidSchedulers.mainThread().scheduleDirect(() -> {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss();
            setFullScreenOrNot(false);
        }, 3, TimeUnit.SECONDS);
    }
}
