package com.example.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.R

class MusicManager {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    fun start(context: Context) {
        if (isPlaying) return
        try {
            val mp = MediaPlayer.create(context.applicationContext, R.raw.music_bg)
            if (mp == null) {
                Log.e("MusicManager", "Failed to create MediaPlayer for music_bg")
                return
            }
            mp.isLooping = true
            mp.setVolume(0.4f, 0.4f)
            mp.setOnErrorListener { _, what, extra ->
                Log.e("MusicManager", "MediaPlayer error what=$what extra=$extra")
                stop()
                true
            }
            mp.start()
            mediaPlayer = mp
            isPlaying = true
        } catch (e: Exception) {
            Log.e("MusicManager", "Failed to start music", e)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("MusicManager", "Failed to stop music", e)
        }
        mediaPlayer = null
        isPlaying = false
    }

    fun isPlaying(): Boolean = isPlaying
}
