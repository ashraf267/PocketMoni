package com.theonlyashraf.pocketmoni.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.theonlyashraf.pocketmoni.R

/**
 * Low-latency audio engine for instant credit alerts on Android POS terminals.
 *
 * Uses [SoundPool] instead of [android.media.MediaPlayer] to achieve sub-50ms
 * acoustic feedback latency by preloading the notification sound buffer into RAM.
 */
class CreditSoundPlayer(private val context: Context) {

    private companion object {
        private const val TAG = "CreditSoundPlayer"
        private const val MAX_STREAMS = 3
    }

    private val soundPool: SoundPool
    private var soundId: Int = 0
    private var isLoaded: Boolean = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                isLoaded = true
                Log.d(TAG, "Credit chime audio loaded successfully (sampleId: $sampleId)")
            } else {
                Log.e(TAG, "Failed to load credit chime audio. Status code: $status")
            }
        }

        try {
            soundId = soundPool.load(context, R.raw.lock_chime, 1)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading sound resource R.raw.lock_chime", e)
        }
    }

    /**
     * Triggers immediate acoustic feedback for a verified credit transaction.
     */
    fun playChime() {
        if (isLoaded && soundId != 0) {
            soundPool.play(
                soundId,
                1.0f, // Left volume
                1.0f, // Right volume
                1,    // Priority
                0,    // Loop (0 = no loop)
                1.0f  // Playback rate (normal speed)
            )
            Log.d(TAG, "Credit alert chime played!")
        } else {
            Log.w(TAG, "Chime requested before audio buffer finished loading")
        }
    }

    /**
     * Releases audio hardware resources when the screen/service is destroyed.
     */
    fun release() {
        try {
            soundPool.release()
            isLoaded = false
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundPool resources", e)
        }
    }
}
