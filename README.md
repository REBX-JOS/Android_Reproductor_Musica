# Android Music Player / Reproductor de Música

A modern Android music player app that automatically scans and organizes your music library.

[Versión en Español](README_ES.md)

## Features

- 🎵 Automatic music library scanning
- 📁 Scans all audio files on your device
- ⭐ Favorites management
- 📋 Playlist creation and management
- 🔍 Search functionality
- 🎨 Album art display
- 🔄 Repeat and shuffle modes
- 📱 Background playback with notifications

## How to Add Music

### Method 1: Copy Music Files to Your Device

The app automatically scans your device storage for audio files. To add music:

1. **Connect your device to a computer** via USB
2. **Copy your music files** (MP3, AAC, FLAC, OGG, WAV, etc.) to any folder on your device:
   - `Music/` folder (recommended)
   - `Download/` folder
   - Any custom folder like `MyMusic/` or `Songs/`
3. **Open the app** and it will automatically detect the new files
4. If songs don't appear, **swipe down** on the library screen to manually refresh

### Method 2: Download Music Directly on Device

1. Download music files using your device's browser or any file manager app
2. Save files to your device storage (any folder)
3. Open the music player app
4. **Swipe down to refresh** the library if needed

### Method 3: Transfer via Bluetooth/WiFi

1. Receive music files via Bluetooth from another device, or
2. Use WiFi file transfer apps (like Files by Google, ShareIt, etc.)
3. Save files to your device storage
4. Open the app and swipe down to refresh

## Supported Audio Formats

The app supports all formats that Android MediaStore recognizes:
- MP3
- AAC / M4A
- FLAC
- OGG / OGG Vorbis
- WAV
- WMA
- And more...

## Using the App

### First Time Setup

1. **Grant Permissions**: On first launch, the app will request permission to access your device storage. Click "Allow" to let the app scan for music files.
2. **Wait for Scan**: The app will automatically scan your device for music files. This may take a few moments depending on how many files you have.
3. **Browse Your Library**: Once scanning is complete, you'll see all your songs in the Library tab.

### Manual Library Refresh

If you add new music files and they don't appear automatically:

1. Go to the **Library** tab
2. **Swipe down** on the screen to pull down and refresh
3. The app will rescan your device storage
4. New songs will appear in your library

### Organizing Your Music

- **Favorites**: Tap the heart icon on any song to add it to your favorites
- **Playlists**: Create custom playlists by going to the Playlists tab
- **Tabs**: Switch between All Songs, Favorites, and Recent tracks using the tabs at the top
- **Search**: Use the search icon to find specific songs, artists, or albums

### Playback Controls

- Tap any song to start playing
- Use the notification controls to play/pause without opening the app
- Access full player controls by tapping the mini player at the bottom
- Enable shuffle or repeat modes from the player screen

## Troubleshooting

### Songs Not Appearing

1. **Check Permissions**: Make sure you've granted storage permission
   - Go to Settings → Apps → Reproductor de Música → Permissions → Storage → Allow
2. **Verify File Format**: Ensure your music files are in a supported format
3. **Check File Location**: Files should be in accessible storage (not hidden or system folders)
4. **Manual Refresh**: Swipe down in the Library tab to trigger a rescan

### No Permission Granted

If you denied storage permission:
1. Go to your device **Settings**
2. Navigate to **Apps** → **Reproductor de Música** → **Permissions**
3. Enable **Storage** or **Media** permission
4. Reopen the app and refresh the library

### Music Files Not Detected

- Ensure files are actually audio files (not renamed or corrupted)
- Check if the files are in a folder that's accessible to Android MediaStore
- Avoid storing music in system or hidden folders (folders starting with `.`)
- Try moving files to the standard `Music/` folder

## Requirements

- Android 7.0 (API 24) or higher
- Storage permission to read audio files
- Notification permission for playback controls (Android 13+)

## Privacy

This app:
- Only accesses audio files on your device
- Does not collect or transmit any personal data
- Does not require internet connection (except for downloading music)
- Stores all data locally on your device

## Technical Details

The app uses:
- **MediaStore API** for fast music file discovery
- **Room Database** for local data storage
- **ExoPlayer (Media3)** for audio playback
- **ID3 tag reading** for detailed metadata extraction

---

Made with ❤️ for music lovers
