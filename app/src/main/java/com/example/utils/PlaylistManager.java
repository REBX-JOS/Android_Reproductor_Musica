package com.example.utils;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.data.entity.Playlist;
import com.example.data.entity.Song;
import com.example.data.repository.MusicRepository;

import java.util.List;

/**
 * Manages playlist operations.
 * Provides a high-level API for creating, modifying, and deleting playlists.
 */
public class PlaylistManager {
    
    private final MusicRepository repository;
    
    public PlaylistManager(Context context) {
        this.repository = new MusicRepository(context);
    }
    
    /**
     * Create a new playlist with the given name.
     */
    public void createPlaylist(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }
        
        Playlist playlist = new Playlist();
        playlist.setName(name.trim());
        repository.insertPlaylist(playlist);
    }
    
    /**
     * Rename an existing playlist.
     */
    public void renamePlaylist(long playlistId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }
        
        Playlist playlist = repository.getPlaylistById(playlistId);
        if (playlist != null) {
            playlist.setName(newName.trim());
            playlist.setDateModified(System.currentTimeMillis());
            repository.updatePlaylist(playlist);
        }
    }
    
    /**
     * Delete a playlist.
     */
    public void deletePlaylist(long playlistId) {
        Playlist playlist = repository.getPlaylistById(playlistId);
        if (playlist != null) {
            repository.deletePlaylist(playlist);
        }
    }
    
    /**
     * Add a song to a playlist.
     */
    public void addSongToPlaylist(long playlistId, long songId) {
        repository.addSongToPlaylist(playlistId, songId);
        
        // Update playlist modification time
        Playlist playlist = repository.getPlaylistById(playlistId);
        if (playlist != null) {
            playlist.setDateModified(System.currentTimeMillis());
            repository.updatePlaylist(playlist);
        }
    }
    
    /**
     * Remove a song from a playlist.
     */
    public void removeSongFromPlaylist(long playlistId, long songId) {
        repository.removeSongFromPlaylist(playlistId, songId);
        
        // Update playlist modification time
        Playlist playlist = repository.getPlaylistById(playlistId);
        if (playlist != null) {
            playlist.setDateModified(System.currentTimeMillis());
            repository.updatePlaylist(playlist);
        }
    }
    
    /**
     * Clear all songs from a playlist.
     */
    public void clearPlaylist(long playlistId) {
        repository.clearPlaylist(playlistId);
        
        // Update playlist modification time
        Playlist playlist = repository.getPlaylistById(playlistId);
        if (playlist != null) {
            playlist.setDateModified(System.currentTimeMillis());
            repository.updatePlaylist(playlist);
        }
    }
    
    /**
     * Get all playlists.
     */
    public LiveData<List<Playlist>> getAllPlaylists() {
        return repository.getAllPlaylists();
    }
    
    /**
     * Get a specific playlist by ID.
     */
    public Playlist getPlaylistById(long playlistId) {
        return repository.getPlaylistById(playlistId);
    }
    
    /**
     * Get all songs in a playlist.
     */
    public LiveData<List<Song>> getSongsInPlaylist(long playlistId) {
        return repository.getSongsInPlaylist(playlistId);
    }
}
