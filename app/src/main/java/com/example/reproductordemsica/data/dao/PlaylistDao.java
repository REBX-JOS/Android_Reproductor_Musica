package com.example.reproductordemsica.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.reproductordemsica.data.entity.Playlist;

import java.util.List;

/**
 * Data Access Object for Playlist entity.
 */
@Dao
public interface PlaylistDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Playlist playlist);
    
    @Update
    void update(Playlist playlist);
    
    @Delete
    void delete(Playlist playlist);
    
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    LiveData<List<Playlist>> getAllPlaylists();
    
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    Playlist getPlaylistById(long playlistId);
    
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    LiveData<Playlist> getPlaylistByIdLive(long playlistId);
    
    @Query("DELETE FROM playlists")
    void deleteAll();
}
