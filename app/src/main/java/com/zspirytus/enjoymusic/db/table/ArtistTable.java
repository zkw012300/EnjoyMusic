package com.zspirytus.enjoymusic.db.table;

import com.zspirytus.enjoymusic.db.greendao.AlbumTableDao;
import com.zspirytus.enjoymusic.db.greendao.ArtistTableDao;
import com.zspirytus.enjoymusic.db.greendao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity
public class ArtistTable {

    @Id
    private long artistId;
    private String mArtistName;
    private int mNumberOfAlbums;
    private int mNumberOfTracks;
    @ToMany
    @JoinEntity(
            entity = JoinArtistToAlbum.class,
            sourceProperty = "artistId",
            targetProperty = "albumId"
    )
    private List<AlbumTable> albumTables;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 284483351)
    private transient ArtistTableDao myDao;

    @Generated(hash = 1289315255)
    public ArtistTable(long artistId, String mArtistName, int mNumberOfAlbums,
                       int mNumberOfTracks) {
        this.artistId = artistId;
        this.mArtistName = mArtistName;
        this.mNumberOfAlbums = mNumberOfAlbums;
        this.mNumberOfTracks = mNumberOfTracks;
    }

    @Generated(hash = 521902308)
    public ArtistTable() {
    }

    public long getArtistId() {
        return this.artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getMArtistName() {
        return this.mArtistName;
    }

    public void setMArtistName(String mArtistName) {
        this.mArtistName = mArtistName;
    }

    public int getMNumberOfAlbums() {
        return this.mNumberOfAlbums;
    }

    public void setMNumberOfAlbums(int mNumberOfAlbums) {
        this.mNumberOfAlbums = mNumberOfAlbums;
    }

    public int getMNumberOfTracks() {
        return this.mNumberOfTracks;
    }

    public void setMNumberOfTracks(int mNumberOfTracks) {
        this.mNumberOfTracks = mNumberOfTracks;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 485269457)
    public List<AlbumTable> getAlbumTables() {
        if (albumTables == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AlbumTableDao targetDao = daoSession.getAlbumTableDao();
            List<AlbumTable> albumTablesNew = targetDao
                    ._queryArtistTable_AlbumTables(artistId);
            synchronized (this) {
                if (albumTables == null) {
                    albumTables = albumTablesNew;
                }
            }
        }
        return albumTables;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1134068986)
    public synchronized void resetAlbumTables() {
        albumTables = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1430391942)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getArtistTableDao() : null;
    }
}