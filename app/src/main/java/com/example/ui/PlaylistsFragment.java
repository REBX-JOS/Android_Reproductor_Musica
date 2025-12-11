package com.example.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.R;

/**
 * Fragment displaying user playlists.
 * Shows list of playlists and allows creation, editing, and deletion.
 */
public class PlaylistsFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // For now, return a simple text view
        // TODO: Implement playlist list view with RecyclerView
        View view = new View(getContext());
        view.setBackgroundColor(0xFFEEEEEE);
        return view;
    }
}
