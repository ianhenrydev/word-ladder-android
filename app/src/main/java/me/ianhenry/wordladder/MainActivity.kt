package me.ianhenry.wordladder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

import me.ianhenry.wordladder.events.WordResultListener
import me.ianhenry.wordladder.fragments.GameFragment
import me.ianhenry.wordladder.fragments.MainMenuFragment
import me.ianhenry.wordladder.fragments.ModeSelectFragment
import me.ianhenry.wordladder.fragments.WordLadderFragment
import me.ianhenry.wordladder.models.Sound
import me.ianhenry.wordladder.tools.WordLadderMediaPlayer
import me.ianhenry.wordladder.tools.WordListener

class MainActivity : AppCompatActivity(), WordResultListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private var recognizer: SpeechRecognizer? = null
    private val PERMISSION_REQUEST_AUDIO = 26
    private var wordIntent: Intent? = null
    private var mDetector: GestureDetectorCompat? = null
    private var currentFragment: WordLadderFragment? = null
    private var mediaPlayer: WordLadderMediaPlayer? = null
    private var listening: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (findViewById<View>(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return
            }
            currentFragment = MainMenuFragment()
            currentFragment!!.arguments = intent.extras
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, currentFragment).commit()
        }

        mediaPlayer = WordLadderMediaPlayer(this)

        checkForPermissions()

        mDetector = GestureDetectorCompat(this, this)
        mDetector!!.setOnDoubleTapListener(this)
    }

    fun startGame(difficulty: Int) {
        val bundle = Bundle()
        bundle.putInt("difficulty", difficulty)
        currentFragment = GameFragment()
        currentFragment!!.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, currentFragment).commit()
    }

    fun chooseMode() {
        currentFragment = ModeSelectFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, currentFragment).commit()
    }

    fun returnToMenu() {
        currentFragment = MainMenuFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, currentFragment).commit()
    }

    fun playBackgroundSound(name: String) {
        mediaPlayer!!.playBackground(name)
    }

    fun playSound(name: String, listener: MediaPlayer.OnCompletionListener?) {
        mediaPlayer!!.play(Sound(Sound.Type.FILE, name, listener))
    }

    fun speak(text: String) {
        mediaPlayer!!.play(Sound(Sound.Type.TTS, text))
    }

    override fun onWordResult(words: Array<String>) {
        currentFragment!!.onSpeechResult(words)
        listening = false
    }

    override fun onError() {
        playSound("PleaseRepeat", null)
        listening = false
    }

    fun speakPrompt(word: String) {
        mediaPlayer!!.play(Sound(Sound.Type.TTS_PROMPT, word, null))
    }

    private fun initSpeechRecognizer() {
        listening = false
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer!!.setRecognitionListener(WordListener(this))

        wordIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        wordIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        wordIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
        wordIntent!!.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 50)
        wordIntent!!.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
    }

    private fun checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_AUDIO)
        } else {
            initSpeechRecognizer()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_AUDIO -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initSpeechRecognizer()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        if (!listening) {
            mediaPlayer!!.stopAndCancel()
            recognizer!!.startListening(wordIntent)
            listening = true
        }
        return false
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        currentFragment!!.onDoubleTap()
        return false
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onDown(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {

    }

    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {

    }

    override fun onFling(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.mDetector!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}
