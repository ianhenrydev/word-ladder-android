package me.ianhenry.wordladder.tools

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

import java.util.ArrayList

import me.ianhenry.wordladder.events.WordResultListener

/**
 * Created by ianhe on 2/22/2017.
 */

class WordListener(private val listener: WordResultListener) : RecognitionListener {
    private val TAG = javaClass.getName()
    override fun onReadyForSpeech(params: Bundle) {
        Log.d(TAG, "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d(TAG, "onRmsChanged")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.d(TAG, "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndofSpeech")
    }

    override fun onError(error: Int) {
        Log.d(TAG, "error $error")
        listener.onError()
    }

    override fun onResults(results: Bundle) {
        val str = String()
        Log.d(TAG, "onResults $results")
        val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val array = data.toTypedArray() as Array<String>
        listener.onWordResult(array)
    }

    override fun onPartialResults(partialResults: Bundle) {
        Log.d(TAG, "onPartialResults")
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        Log.d(TAG, "onEvent $eventType")
    }
}
