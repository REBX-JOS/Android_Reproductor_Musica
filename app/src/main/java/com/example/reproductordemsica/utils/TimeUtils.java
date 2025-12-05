package com.example.reproductordemsica.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for formatting time durations.
 */
public class TimeUtils {
    
    /**
     * Format milliseconds to a readable time string (MM:SS or HH:MM:SS).
     */
    public static String formatDuration(long durationMs) {
        if (durationMs < 0) {
            return "00:00";
        }
        
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60;
        
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Format milliseconds to seconds with one decimal place.
     */
    public static String formatSeconds(long durationMs) {
        double seconds = durationMs / 1000.0;
        return String.format(Locale.getDefault(), "%.1f", seconds);
    }
}
