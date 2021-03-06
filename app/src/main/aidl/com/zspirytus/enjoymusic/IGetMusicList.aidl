// IGetMusicList.aidl
package com.zspirytus.enjoymusic;

import java.util.List;

import com.zspirytus.enjoymusic.db.table.Music;
import com.zspirytus.enjoymusic.db.table.Album;
import com.zspirytus.enjoymusic.db.table.Artist;
import com.zspirytus.enjoymusic.db.table.Folder;

interface IGetMusicList {
    List<Music> getMusicList();
    List<Album> getAlbumList();
    List<Artist> getArtistList();
    List<Folder> getFolderList();
}
