package com.example.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Helper class for managing runtime permissions.
 * Handles storage and notification permissions required by the app.
 */
public class PermissionHelper {
    
    public static final int REQUEST_STORAGE_PERMISSION = 100;
    public static final int REQUEST_NOTIFICATION_PERMISSION = 101;
    
    /**
     * Check if storage permission is granted.
     * Handles different permission requirements for different Android versions.
     */
    public static boolean hasStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: Use READ_MEDIA_AUDIO
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 and below: Use READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    /**
     * Request storage permission from the user.
     */
    public static void requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: Request READ_MEDIA_AUDIO
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Android 12 and below: Request READ_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }
    
    /**
     * Check if we should show permission rationale.
     */
    public static boolean shouldShowStorageRationale(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    
    /**
     * Check if notification permission is granted (Android 13+).
     */
    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;  // Not required on Android 12 and below
    }
    
    /**
     * Request notification permission (Android 13+).
     */
    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION);
        }
    }
    
    /**
     * Check if all required permissions are granted.
     */
    public static boolean hasAllPermissions(Context context) {
        return hasStoragePermission(context) && hasNotificationPermission(context);
    }
}
