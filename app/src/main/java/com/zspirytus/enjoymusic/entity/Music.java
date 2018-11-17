package com.zspirytus.enjoymusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Music实体类
 * Created by ZSpirytus on 2018/8/4.
 */

public class Music implements Parcelable {

    private String musicFilePath;
    private String musicName;
    private String musicAlbumName;
    private String musicThumbAlbumCoverPath;
    private String musicArtist;
    private long musicDuration;
    private String musicFileSize;

    public Music(String musicFilePath, String musicName, String musicArtist, String musicAlbumName, String musicThumbAlbumCoverPath, long musicDuration, String musicFileSize) {
        this.musicFilePath = musicFilePath;
        this.musicName = musicName;
        this.musicAlbumName = musicAlbumName;
        this.musicThumbAlbumCoverPath = musicThumbAlbumCoverPath;
        this.musicArtist = musicArtist;
        this.musicDuration = musicDuration;
        this.musicFileSize = musicFileSize;
    }

    private Music(Parcel source) {
        this.musicFilePath = source.readString();
        this.musicName = source.readString();
        this.musicAlbumName = source.readString();
        this.musicThumbAlbumCoverPath = source.readString();
        this.musicArtist = source.readString();
        this.musicDuration = source.readLong();
        this.musicFileSize = source.readString();
    }

    public String getMusicFilePath() {
        return musicFilePath;
    }


    public String getMusicName() {
        return musicName;
    }

    public String getMusicAlbumName() {
        return musicAlbumName;
    }

    public String getMusicThumbAlbumCoverPath() {
        return musicThumbAlbumCoverPath;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public String getMusicFileSize() {
        return musicFileSize;
    }

    public long getMusicDuration() {
        return musicDuration;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Music && this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (musicFilePath != null) {
            stringBuilder.append("musicFilePath:" + musicFilePath + ", ");
        } else {
            stringBuilder.append("musicFilePath:null, ");
        }
        if (musicName != null) {
            stringBuilder.append("musicName:" + musicName + ", ");
        } else {
            stringBuilder.append("musicName:null, ");
        }
        if (musicAlbumName != null) {
            stringBuilder.append("musicThumbAlbumCoverPath:" + musicThumbAlbumCoverPath + ", ");
        }
        if (musicThumbAlbumCoverPath != null) {
            stringBuilder.append("musicAlbumName:" + musicAlbumName + ", ");
        } else {
            stringBuilder.append("musicAlbumName:null, ");
        }
        if (musicArtist != null) {
            stringBuilder.append("musicArtist:" + musicArtist + ", ");
        } else {
            stringBuilder.append("musicArtist:null, ");
        }
        if (musicDuration != 0) {
            stringBuilder.append("musicDuration:" + musicDuration + ", ");
        } else {
            stringBuilder.append("musicDuration:0, ");
        }
        if (musicFileSize != null) {
            stringBuilder.append("musicFileSize:" + musicFileSize + " ");
        } else {
            stringBuilder.append("musicFileSize:null");
        }
        return "{" + stringBuilder.toString() + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(musicFilePath);
        dest.writeString(musicName);
        dest.writeString(musicAlbumName);
        dest.writeString(musicThumbAlbumCoverPath);
        dest.writeString(musicArtist);
        dest.writeLong(musicDuration);
        dest.writeString(musicFileSize);
    }

    public static final Parcelable.Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[0];
        }
    };
}
