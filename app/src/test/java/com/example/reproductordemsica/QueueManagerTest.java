package com.example.reproductordemsica;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.reproductordemsica.data.entity.Song;
import com.example.reproductordemsica.utils.QueueManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for QueueManager.
 */
@RunWith(AndroidJUnit4.class)
public class QueueManagerTest {
    
    private QueueManager queueManager;
    private List<Song> testSongs;
    
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        queueManager = new QueueManager(context);
        
        // Create test songs
        testSongs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Song song = new Song();
            song.setId(i);
            song.setTitle("Song " + i);
            song.setArtist("Artist " + i);
            song.setPath("/path/to/song" + i + ".mp3");
            testSongs.add(song);
        }
    }
    
    @Test
    public void testSetQueue() {
        queueManager.setQueue(testSongs, 0);
        
        assertEquals(5, queueManager.getQueueSize());
        assertEquals(0, queueManager.getCurrentIndex());
        assertNotNull(queueManager.getCurrentSong());
        assertEquals("Song 0", queueManager.getCurrentSong().getTitle());
    }
    
    @Test
    public void testNext() {
        queueManager.setQueue(testSongs, 0);
        
        Song nextSong = queueManager.next();
        assertNotNull(nextSong);
        assertEquals("Song 1", nextSong.getTitle());
        assertEquals(1, queueManager.getCurrentIndex());
    }
    
    @Test
    public void testPrevious() {
        queueManager.setQueue(testSongs, 2);
        
        Song prevSong = queueManager.previous();
        assertNotNull(prevSong);
        assertEquals("Song 1", prevSong.getTitle());
        assertEquals(1, queueManager.getCurrentIndex());
    }
    
    @Test
    public void testRepeatOne() {
        queueManager.setQueue(testSongs, 0);
        queueManager.setRepeatMode(QueueManager.REPEAT_ONE);
        
        Song currentSong = queueManager.getCurrentSong();
        Song nextSong = queueManager.next();
        
        // Should return same song
        assertEquals(currentSong.getId(), nextSong.getId());
        assertEquals(0, queueManager.getCurrentIndex());
    }
    
    @Test
    public void testRepeatAll() {
        queueManager.setQueue(testSongs, 4); // Last song
        queueManager.setRepeatMode(QueueManager.REPEAT_ALL);
        
        Song nextSong = queueManager.next();
        
        // Should wrap to first song
        assertNotNull(nextSong);
        assertEquals("Song 0", nextSong.getTitle());
        assertEquals(0, queueManager.getCurrentIndex());
    }
    
    @Test
    public void testAddToQueue() {
        queueManager.setQueue(testSongs, 0);
        
        Song newSong = new Song();
        newSong.setId(100);
        newSong.setTitle("New Song");
        
        queueManager.addToQueue(newSong);
        
        assertEquals(6, queueManager.getQueueSize());
    }
    
    @Test
    public void testRemoveAt() {
        queueManager.setQueue(testSongs, 0);
        
        queueManager.removeAt(2);
        
        assertEquals(4, queueManager.getQueueSize());
    }
    
    @Test
    public void testClearQueue() {
        queueManager.setQueue(testSongs, 0);
        queueManager.clearQueue();
        
        assertEquals(0, queueManager.getQueueSize());
        assertNull(queueManager.getCurrentSong());
    }
}
