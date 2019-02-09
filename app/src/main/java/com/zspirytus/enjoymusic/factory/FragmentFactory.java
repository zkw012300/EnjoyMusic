package com.zspirytus.enjoymusic.factory;

import android.util.SparseArray;

import com.zspirytus.enjoymusic.base.BaseFragment;
import com.zspirytus.enjoymusic.view.fragment.AboutFragment;
import com.zspirytus.enjoymusic.view.fragment.AlbumMusicListFragment;
import com.zspirytus.enjoymusic.view.fragment.AllMusicListFragment;
import com.zspirytus.enjoymusic.view.fragment.ArtistMusicListFragment;
import com.zspirytus.enjoymusic.view.fragment.FolderSortedMusicListFragment;
import com.zspirytus.enjoymusic.view.fragment.HomePageFragment;
import com.zspirytus.enjoymusic.view.fragment.MusicCategoryFragment;
import com.zspirytus.enjoymusic.view.fragment.MusicPlayFragment;
import com.zspirytus.enjoymusic.view.fragment.PlayListFragment;
import com.zspirytus.enjoymusic.view.fragment.SettingsFragment;

/**
 * Created by ZSpirytus on 2018/9/14.
 */

public class FragmentFactory {

    private static final FragmentFactory INSTANCE = new FragmentFactory();
    private SparseArray<Class> fragments;

    private HomePageFragment mHomePageFragment;
    private MusicCategoryFragment mMusicCategoryFragment;
    private SettingsFragment mSettingsFragment;
    private AboutFragment mAboutFragment;
    private MusicPlayFragment mMusicPlayFragment;
    private AllMusicListFragment mAllMusicListFragment;
    private AlbumMusicListFragment mAlbumMusicListFragment;
    private ArtistMusicListFragment mArtistMusicListFragment;
    private PlayListFragment mPlayListFragment;
    private FolderSortedMusicListFragment mFolderSortedMusicListFragment;

    private FragmentFactory() {
        fragments = new SparseArray<>();
        fragments.put(HomePageFragment.class.getSimpleName().hashCode(), HomePageFragment.class);
        fragments.put(MusicCategoryFragment.class.getSimpleName().hashCode(), MusicCategoryFragment.class);
        fragments.put(SettingsFragment.class.getSimpleName().hashCode(), SettingsFragment.class);
        fragments.put(AboutFragment.class.getSimpleName().hashCode(), AboutFragment.class);
        fragments.put(MusicPlayFragment.class.getSimpleName().hashCode(), MusicPlayFragment.class);
        fragments.put(AllMusicListFragment.class.getSimpleName().hashCode(), AllMusicListFragment.class);
        fragments.put(AlbumMusicListFragment.class.getSimpleName().hashCode(), AlbumMusicListFragment.class);
        fragments.put(ArtistMusicListFragment.class.getSimpleName().hashCode(), ArtistMusicListFragment.class);
        fragments.put(PlayListFragment.class.getSimpleName().hashCode(), PlayListFragment.class);
        fragments.put(FolderSortedMusicListFragment.class.getSimpleName().hashCode(), FolderSortedMusicListFragment.class);
    }

    public static FragmentFactory getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public BaseFragment get(String fragmentName) {
        return get(fragments.get(fragmentName.hashCode()));
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseFragment> T get(Class<T> tClass) {
        if (tClass == HomePageFragment.class) {
            if (mHomePageFragment == null) {
                mHomePageFragment = HomePageFragment.getInstance();
            }
            return (T) mHomePageFragment;
        } else if (tClass == MusicCategoryFragment.class) {
            if (mMusicCategoryFragment == null) {
                mMusicCategoryFragment = MusicCategoryFragment.getInstance();
            }
            return (T) mMusicCategoryFragment;
        } else if (tClass == SettingsFragment.class) {
            if (mSettingsFragment == null) {
                mSettingsFragment = SettingsFragment.getInstance();
            }
            return (T) mSettingsFragment;
        } else if (tClass == AboutFragment.class) {
            if (mAboutFragment == null) {
                mAboutFragment = AboutFragment.getInstance();
            }
            return (T) mAboutFragment;
        } else if (tClass == MusicPlayFragment.class) {
            if (mMusicPlayFragment == null) {
                mMusicPlayFragment = MusicPlayFragment.getInstance();
            }
            return (T) mMusicPlayFragment;
        } else if (tClass == AllMusicListFragment.class) {
            if (mAllMusicListFragment == null) {
                mAllMusicListFragment = AllMusicListFragment.getInstance();
            }
            return (T) mAllMusicListFragment;
        } else if (tClass == AlbumMusicListFragment.class) {
            if (mAlbumMusicListFragment == null) {
                mAlbumMusicListFragment = AlbumMusicListFragment.getInstance();
            }
            return (T) mAlbumMusicListFragment;
        } else if (tClass == ArtistMusicListFragment.class) {
            if (mArtistMusicListFragment == null) {
                mArtistMusicListFragment = ArtistMusicListFragment.getInstance();
            }
            return (T) mArtistMusicListFragment;
        } else if (tClass == PlayListFragment.class) {
            if (mPlayListFragment == null)
                mPlayListFragment = PlayListFragment.getInstance();
            return (T) mPlayListFragment;
        } else if (tClass == FolderSortedMusicListFragment.class) {
            if (mFolderSortedMusicListFragment == null)
                mFolderSortedMusicListFragment = FolderSortedMusicListFragment.getInstance();
            return (T) mFolderSortedMusicListFragment;
        }
        return null;
    }
}
