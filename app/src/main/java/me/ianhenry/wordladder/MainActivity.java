package me.ianhenry.wordladder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

import me.ianhenry.wordladder.events.WordResultListener;
import me.ianhenry.wordladder.fragments.GameFragment;
import me.ianhenry.wordladder.fragments.MainMenuFragment;
import me.ianhenry.wordladder.fragments.WordLadderFragment;
import me.ianhenry.wordladder.models.Sound;
import me.ianhenry.wordladder.tools.WordLadderMediaPlayer;
import me.ianhenry.wordladder.tools.WordListener;

public class MainActivity extends AppCompatActivity implements WordResultListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private SpeechRecognizer recognizer;
    private final int PERMISSION_REQUEST_AUDIO = 26;
    private Intent wordIntent;
    private GestureDetectorCompat mDetector;;
    private WordLadderFragment currentFragment;
    private WordLadderMediaPlayer mediaPlayer;
    private boolean listening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            currentFragment = new MainMenuFragment();
            currentFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, currentFragment).commit();
        }

        mediaPlayer = new WordLadderMediaPlayer(this);

        checkForPermissions();

        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);
    }

    public void startGame() {
        currentFragment = new GameFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    public void playSound(String name, MediaPlayer.OnCompletionListener listener) {
        mediaPlayer.play(new Sound(Sound.Type.FILE, name, listener));
    }

    public void speak(String text) {
        mediaPlayer.play(new Sound(Sound.Type.TTS, text));
    }

    @Override
    public void onWordResult(String[] words) {
        currentFragment.onSpeechResult(words);
        listening = false;
    }

    @Override
    public void onError() {
        playSound("PleaseRepeat", null);
        listening = false;
    }

    public void speakPrompt(String word) {
        mediaPlayer.play(new Sound(Sound.Type.TTS_PROMPT, word, null));
    }

    private void initSpeechRecognizer() {
        listening = false;
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new WordListener(this));

        wordIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        wordIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        wordIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        wordIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 50);
        wordIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_AUDIO);
        } else {
            initSpeechRecognizer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initSpeechRecognizer();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (!listening) {
            mediaPlayer.stopAndCancel();
            recognizer.startListening(wordIntent);
            listening = true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        currentFragment.onDoubleTap();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
