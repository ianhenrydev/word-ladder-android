package me.ianhenry.wordladder.models

import android.media.MediaPlayer

/**
 * Created by ianhe on 5/6/2017.
 */

class Sound @JvmOverloads constructor(val type: Type, val text: String, val listener: MediaPlayer.OnCompletionListener? = null) {
    enum class Type {
        FILE, TTS, TTS_PROMPT
    }

    companion object {
        val WELCOME = "Welcome"
    }
}
