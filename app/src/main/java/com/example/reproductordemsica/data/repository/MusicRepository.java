package com.example.reproductordemsica.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.reproductordemsica.data.dao.PlaylistDao;
import com.example.reproductordemsica.data.dao.PlaylistSongDao;
import com.example.reproductordemsica.data.dao.SongDao;
import com.example.reproductordemsica.data.database.MusicDatabase;
import com.example.reproductordemsica.data.entity.Playlist;
import com.example.reproductordemsica.data.entity.PlaylistSong;
import com.example.reproductordemsica.data.entity.Song;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for managing music library data.
 * Provides a clean API for the rest of the app to interact with the database.
 */
public class MusicRepository {
    
    private final SongDao songDao;
    private final PlaylistDao playlistDao;
    private final PlaylistSongDao playlistSongDao;
    private final ExecutorService executorService;
    
    public MusicRepository(Context context) {
        MusicDatabase database = MusicDatabase.getInstance(context);
        songDao = database.songDao();
        playlistDao = database.playlistDao();
        playlistSongDao = database.playlistSongDao();
        executorService = Executors.newFixedThreadPool(4);
    }
    
    // Song operations
    public LiveData<List<Song>> getAllSongs() {
        return songDao.getAllSongs();
    }
    
    public LiveData<List<Song>> getFavoriteSongs() {
        return songDao.getFavoriteSongs();
    }
    
    public LiveData<List<Song>> getRecentSongs() {
        return songDao.getRecentSongs();
    }
    
    public LiveData<List<Song>> searchSongs(String query) {
        return songDao.searchSongs("%" + query + "%");
    }
    
    public Song getSongById(long songId) {
        return songDao.getSongById(songId);
    }
    
    public void insertSong(Song song) {
        executorService.execute(() -> songDao.insert(song));
    }
    
    public void insertSongs(List<Song> songs) {
        executorService.execute(() -> songDao.insertAll(songs));
    }
    
    public void updateSong(Song song) {
        executorService.execute(() -> songDao.update(song));
    }
    
    public void updateFavoriteStatus(long songId, boolean isFavorite) {
        executorService.execute(() -> songDao.updateFavoriteStatus(songId, isFavorite));
    }
    
    public void updatePlaybackPosition(long songId, long position, long timestamp) {
        executorService.execute(() -> songDao.updatePlaybackPosition(songId, position, timestamp));
    }
    
    public void deleteSong(Song song) {
        executorService.execute(() -> songDao.delete(song));
    }
    
    // Playlist operations
    public LiveData<List<Playlist>> getAllPlaylists() {
        return playlistDao.getAllPlaylists();
    }
    
    public Playlist getPlaylistById(long playlistId) {
        return playlistDao.getPlaylistById(playlistId);
    }
    
    public void insertPlaylist(Playlist playlist) {
        executorService.execute(() -> playlistDao.insert(playlist));
    }
    
    public void updatePlaylist(Playlist playlist) {
        executorService.execute(() -> playlistDao.update(playlist));
    }
    
    public void deletePlaylist(Playlist playlist) {
        executorService.execute(() -> playlistDao.delete(playlist));
    }
    
    // Playlist-Song relationship operations
    public LiveData<List<Song>> getSongsInPlaylist(long playlistId) {
        return playlistSongDao.getSongsInPlaylist(playlistId);
    }
    
    public void addSongToPlaylist(long playlistId, long songId) {
        executorService.execute(() -> {
            Integer maxPos = playlistSongDao.getMaxPosition(playlistId);
            int position = (maxPos == null) ? 0 : maxPos + 1;
            PlaylistSong playlistSong = new PlaylistSong(playlistId, songId, position);
            playlistSongDao.insert(playlistSong);
        });
    }
    
    public void removeSongFromPlaylist(long playlistId, long songId) {
        executorService.execute(() -> playlistSongDao.delete(playlistId, songId));
    }
    
    public void clearPlaylist(long playlistId) {
        executorService.execute(() -> playlistSongDao.deleteAllFromPlaylist(playlistId));
    }
}
