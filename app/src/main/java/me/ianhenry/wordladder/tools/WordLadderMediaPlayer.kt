package me.ianhenry.wordladder.tools

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log

import java.io.IOException
import java.util.HashMap
import java.util.LinkedList
import java.util.Locale

import me.ianhenry.wordladder.models.Sound

/**
 * Created by ianhe on 5/6/2017.
 */

class WordLadderMediaPlayer(private val context: Context) : UtteranceProgressListener(), MediaPlayer.OnCompletionListener, TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var backgroundMediaPlayer: MediaPlayer? = null
    private val soundQueue: LinkedList<Sound>
    private var currentSound: Sound? = null
    private val textToSpeech: TextToSpeech
    private var ready: Boolean = false

    init {
        mediaPlayer = MediaPlayer()
        soundQueue = LinkedList<Sound>()
        textToSpeech = TextToSpeech(context, this)
        ready = false
    }

    override fun onInit(status: Int) {
        textToSpeech.setLanguage(Locale.US)
        textToSpeech.setSpeechRate(1f)
        textToSpeech.setOnUtteranceProgressListener(this)
        ready = true
        if (!mediaPlayer!!.isPlaying() && !textToSpeech.isSpeaking() && soundQueue.size > 0) {
            play(soundQueue.remove())
        }
    }

    fun play(sound: Sound) {
        if (mediaPlayer!!.isPlaying() || textToSpeech.isSpeaking() || !ready) {
            soundQueue.add(sound)
        } else {
            currentSound = sound
            when (currentSound!!.type) {
                Sound.Type.FILE -> playFile()
                Sound.Type.TTS -> speakText()
                Sound.Type.TTS_PROMPT -> speakPrompt()
            }
        }
    }

    fun playBackground(name: String) {
        try {
            val descriptor = context.getAssets().openFd("sounds/$name.mp3")
            backgroundMediaPlayer = MediaPlayer()
            backgroundMediaPlayer!!.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength())
            descriptor.close()
            backgroundMediaPlayer!!.prepare()
            backgroundMediaPlayer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun playFile() {
        try {
            val descriptor = context.getAssets().openFd("sounds/" + currentSound!!.text + ".mp3")
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength())
            descriptor.close()
            mediaPlayer!!.prepare()
            mediaPlayer!!.setOnCompletionListener(this)
            mediaPlayer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun speakText() {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "messageID"
        textToSpeech.speak(currentSound!!.text, TextToSpeech.QUEUE_FLUSH, map)
    }

    private fun speakPrompt() {
        val word = currentSound!!.text
        textToSpeech.speak(word.toLowerCase(), TextToSpeech.QUEUE_ADD, null, null)
        for (i in 0 until word.length) {
            textToSpeech.playSilentUtterance(100, TextToSpeech.QUEUE_ADD, null)
            textToSpeech.speak(word.get(i) + "", TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    fun stopAndCancel() {
        if (mediaPlayer!!.isPlaying()) {
            mediaPlayer!!.stop()
        }
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop()
        }
        while (soundQueue.size > 0) {
            soundQueue.remove()
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        if (currentSound!!.listener != null) {
            currentSound!!.listener!!.onCompletion(mp)
        }
        if (soundQueue.size > 0) {
            play(soundQueue.remove())
        }
    }

    override fun onStart(utteranceId: String) {
        Log.d("Utterance", "onStart")
    }

    override fun onDone(utteranceId: String) {
        if (soundQueue.size > 0) {
            play(soundQueue.remove())
        }
    }

    override fun onError(utteranceId: String) {
        Log.d("Utterance", "onError")
    }
}
