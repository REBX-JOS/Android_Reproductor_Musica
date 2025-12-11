package com.example.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.data.entity.Song;

import java.util.List;

/**
 * Data Access Object for Song entity.
 * Provides methods to interact with the songs table in the database.
 */
@Dao
public interface SongDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Song song);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Song> songs);
    
    @Update
    void update(Song song);
    
    @Delete
    void delete(Song song);
    
    @Query("SELECT * FROM songs ORDER BY title ASC")
    LiveData<List<Song>> getAllSongs();
    
    @Query("SELECT * FROM songs WHERE id = :songId")
    Song getSongById(long songId);
    
    @Query("SELECT * FROM songs WHERE id = :songId")
    LiveData<Song> getSongByIdLive(long songId);
    
    @Query("SELECT * FROM songs WHERE isFavorite = 1 ORDER BY title ASC")
    LiveData<List<Song>> getFavoriteSongs();
    
    @Query("SELECT * FROM songs ORDER BY lastPlayed DESC LIMIT 50")
    LiveData<List<Song>> getRecentSongs();
    
    @Query("SELECT * FROM songs WHERE title LIKE :query OR artist LIKE :query OR album LIKE :query")
    LiveData<List<Song>> searchSongs(String query);
    
    @Query("UPDATE songs SET isFavorite = :isFavorite WHERE id = :songId")
    void updateFavoriteStatus(long songId, boolean isFavorite);
    
    @Query("UPDATE songs SET playbackPosition = :position, lastPlayed = :timestamp WHERE id = :songId")
    void updatePlaybackPosition(long songId, long position, long timestamp);
    
    @Query("DELETE FROM songs WHERE path = :path")
    void deleteByPath(String path);
    
    @Query("DELETE FROM songs")
    void deleteAll();
    
    @Query("SELECT COUNT(*) FROM songs")
    int getSongCount();
}
