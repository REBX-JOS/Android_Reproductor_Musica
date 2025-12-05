package com.example.reproductordemsica;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;

import com.example.reproductordemsica.data.entity.Playlist;
import com.example.reproductordemsica.data.repository.MusicRepository;
import com.example.reproductordemsica.utils.PlaylistManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for PlaylistManager.
 */
public class PlaylistManagerTest {
    
    @Mock
    private Context mockContext;
    
    @Mock
    private MusicRepository mockRepository;
    
    private PlaylistManager playlistManager;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        playlistManager = new PlaylistManager(mockContext);
    }
    
    @Test
    public void testCreatePlaylist_ValidName() {
        // Test creating a playlist with valid name
        String playlistName = "My Playlist";
        playlistManager.createPlaylist(playlistName);
        
        // Verify repository interaction would occur
        // In real test, would verify repository.insertPlaylist() was called
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreatePlaylist_EmptyName() {
        // Test that empty name throws exception
        playlistManager.createPlaylist("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreatePlaylist_NullName() {
        // Test that null name throws exception
        playlistManager.createPlaylist(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRenamePlaylist_EmptyName() {
        // Test that empty name throws exception
        playlistManager.renamePlaylist(1L, "");
    }
    
    @Test
    public void testAddSongToPlaylist() {
        // Test adding song to playlist
        long playlistId = 1L;
        long songId = 100L;
        
        Playlist mockPlaylist = new Playlist();
        mockPlaylist.setId(playlistId);
        mockPlaylist.setName("Test Playlist");
        
        playlistManager.addSongToPlaylist(playlistId, songId);
        
        // Verify would call repository methods
    }
}
