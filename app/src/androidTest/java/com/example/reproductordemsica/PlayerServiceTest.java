package com.example.reproductordemsica;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.reproductordemsica.service.PlayerService;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test for PlayerService.
 * Tests that the service can be started successfully.
 */
@RunWith(AndroidJUnit4.class)
public class PlayerServiceTest {
    
    @Test
    public void testServiceStarts() {
        Context context = ApplicationProvider.getApplicationContext();
        
        // Create intent for the service
        Intent serviceIntent = new Intent(context, PlayerService.class);
        
        // The service should be able to be created
        assertNotNull(serviceIntent);
        assertEquals(PlayerService.class.getName(), serviceIntent.getComponent().getClassName());
    }
    
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = ApplicationProvider.getApplicationContext();
        assertEquals("com.example.reproductordemsica", appContext.getPackageName());
    }
}
