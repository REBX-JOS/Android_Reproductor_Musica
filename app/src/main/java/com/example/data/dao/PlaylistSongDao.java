package com.example.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.data.entity.PlaylistSong;
import com.example.data.entity.Song;

import java.util.List;

/**
 * Data Access Object for PlaylistSong entity.
 * Manages the many-to-many relationship between playlists and songs.
 */
@Dao
public interface PlaylistSongDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlaylistSong playlistSong);
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    void delete(long playlistId, long songId);
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    void deleteAllFromPlaylist(long playlistId);
    
    @Transaction
    @Query("SELECT songs.* FROM songs " +
           "INNER JOIN playlist_songs ON songs.id = playlist_songs.songId " +
           "WHERE playlist_songs.playlistId = :playlistId " +
           "ORDER BY playlist_songs.position ASC")
    LiveData<List<Song>> getSongsInPlaylist(long playlistId);
    
    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    int getSongCountInPlaylist(long playlistId);
    
    @Query("SELECT MAX(position) FROM playlist_songs WHERE playlistId = :playlistId")
    Integer getMaxPosition(long playlistId);
}
