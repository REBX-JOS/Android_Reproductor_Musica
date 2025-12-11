package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.utils.PermissionHelper;

/**
 * Splash screen shown on app launch.
 * Handles initial permission requests and transitions to MainActivity.
 */
public class SplashActivity extends AppCompatActivity {
    
    private static final long SPLASH_DELAY = 2000; // 2 seconds
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Check permissions after a short delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkPermissionsAndProceed();
        }, SPLASH_DELAY);
    }
    
    /**
     * Check if required permissions are granted and proceed accordingly.
     */
    private void checkPermissionsAndProceed() {
        if (PermissionHelper.hasStoragePermission(this)) {
            // Permissions granted, proceed to main activity
            proceedToMainActivity();
        } else {
            // Show rationale and request permissions
            showPermissionRationale();
        }
    }
    
    /**
     * Show rationale dialog before requesting permissions.
     */
    private void showPermissionRationale() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.storage_permission_rationale)
                .setPositiveButton(R.string.grant_permission, (dialog, which) -> {
                    PermissionHelper.requestStoragePermission(this);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // User denied, but still proceed (they can grant later)
                    proceedToMainActivity();
                })
                .setCancelable(false)
                .show();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PermissionHelper.REQUEST_STORAGE_PERMISSION) {
            // Regardless of result, proceed to main activity
            // User can grant permission later from settings
            proceedToMainActivity();
        }
    }
    
    /**
     * Transition to MainActivity.
     */
    private void proceedToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
