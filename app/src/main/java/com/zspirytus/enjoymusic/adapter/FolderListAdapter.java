package com.zspirytus.enjoymusic.adapter;

import android.view.View;

import com.zspirytus.basesdk.recyclerview.adapter.CommonRecyclerViewAdapter;
import com.zspirytus.basesdk.recyclerview.listeners.OnItemLongClickListener;
import com.zspirytus.basesdk.recyclerview.viewholder.CommonViewHolder;
import com.zspirytus.enjoymusic.R;
import com.zspirytus.enjoymusic.cache.constant.Constant;
import com.zspirytus.enjoymusic.db.QueryExecutor;
import com.zspirytus.enjoymusic.db.table.Album;
import com.zspirytus.enjoymusic.db.table.Folder;
import com.zspirytus.enjoymusic.db.table.Music;
import com.zspirytus.enjoymusic.engine.ForegroundMusicController;
import com.zspirytus.enjoymusic.engine.FragmentVisibilityManager;
import com.zspirytus.enjoymusic.engine.ImageLoader;
import com.zspirytus.enjoymusic.global.MainApplication;
import com.zspirytus.enjoymusic.utils.ToastUtil;
import com.zspirytus.enjoymusic.view.dialog.PlainTextMenuDialog;
import com.zspirytus.enjoymusic.view.dialog.SaveSongListDialog;

import java.util.List;

public class FolderListAdapter extends CommonRecyclerViewAdapter<Folder> implements OnItemLongClickListener {

    @Override
    public int getLayoutId() {
        return R.layout.item_common_view_type;
    }

    @Override
    public void convert(CommonViewHolder holder, Folder folder, int position) {
        List<Music> musicList = QueryExecutor.findMusicList(folder);
        Music firstMusicInFolder = musicList.get(0);
        Album album = QueryExecutor.findAlbum(firstMusicInFolder);
        String coverPath = album.getArtPath();
        ImageLoader.load(holder.getView(R.id.item_cover), coverPath, folder.getFolderName());
        holder.setText(R.id.item_title, folder.getFolderName());
        holder.setText(R.id.item_sub_title, folder.getFolderDir());
        if (mListener != null) {
            holder.setOnItemClickListener(mListener);
        }
        holder.getView(R.id.item_more_info_button).setOnClickListener(v -> showDialog(position));
    }

    @Override
    public void onLongClick(View view, int position) {
        showDialog(position);
    }

    private void showDialog(int position) {
        targetFolder = getList().get(position);
        PlainTextMenuDialog dialog = PlainTextMenuDialog.create(targetFolder.getFolderDir(), Constant.MenuTexts.folderMenuTexts);
        dialog.setOnMenuItemClickListener(listener);
        FragmentVisibilityManager.getInstance().showDialogFragment(dialog);
    }

    private Folder targetFolder;

    private PlainTextMenuDialog.OnMenuItemClickListener listener = (menuText, pos) -> {
        switch (pos) {
            case 0:
                List<Music> musicList = QueryExecutor.findMusicList(targetFolder);
                ForegroundMusicController.getInstance().addToPlayList(musicList);
                ToastUtil.showToast(MainApplication.getForegroundContext(), R.string.success);
                break;
            case 1:
                SaveSongListDialog dialog = new SaveSongListDialog();
                dialog.setOnDialogButtonClickListener(content -> {
                    if (content != null) {
                        dialog.dismiss();
                    } else {
                        ToastUtil.showToast(MainApplication.getForegroundContext(), R.string.please_enter_leagl_song_list);
                    }
                });
                FragmentVisibilityManager.getInstance().showDialogFragment(dialog);
                break;
        }
    };
}
