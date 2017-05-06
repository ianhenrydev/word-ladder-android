package me.ianhenry.wordladder;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.io.IOException;
import java.util.ArrayList;

import me.ianhenry.wordladder.events.WordResultListener;
import me.ianhenry.wordladder.fragments.GameFragment;
import me.ianhenry.wordladder.fragments.MainMenuFragment;
import me.ianhenry.wordladder.fragments.WordLadderFragment;
import me.ianhenry.wordladder.models.Sound;
import me.ianhenry.wordladder.tools.WordDatabaseHelper;
import me.ianhenry.wordladder.tools.WordLadderMediaPlayer;
import me.ianhenry.wordladder.tools.WordListener;

public class MainActivity extends AppCompatActivity implements WordResultListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private SpeechRecognizer recognizer;
    private final int PERMISSION_REQUEST_AUDIO = 26;
    private Intent wordIntent;
    private Boolean listening = false;
    private Cursor resultCursor;
    private int score;
    private GestureDetectorCompat mDetector;
    private Status STATUS;
    private int difficulty;
    private String correctWord;
    private CountDownTimer countDownTimer;
    private boolean endGame = false;
    private WordLadderFragment currentFragment;
    private WordLadderMediaPlayer mediaPlayer;

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

        score = 0;
    }

    private enum Status {
        MAIN_MENU, DIFFICULTY, GAME
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



    private void increaseScore() {
        score++;
    }

    @Override
    public void onWordResult(String[] words) {
        currentFragment.onSpeechResult(words);
    }
    private void menuWordResult(String[] words) {
        for (String word : words) {
           switch (word.toUpperCase()) {
               case "PLAY":
                   playSound("SelectDifficulty", null);
                   STATUS = Status.DIFFICULTY;
                   break;
               case "TUTORIAL":
                   playSound("TutorialReadOut", null);
                   break;
               case "LEADERBOARD":
                   playSound("1stPlace", new MediaPlayer.OnCompletionListener() {
                       @Override
                       public void onCompletion(MediaPlayer mediaPlayer) {
                           SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
                           //speak(sp.getInt("high_score", 0) + " points");
                       }
                   });
                   break;
           }
        }
        listening = false;
    }
    private void difficultyWordResult(String[] words) {
        for (String word : words) {
            switch (word.toUpperCase()) {
                case "EASY":
                    startGame(3);
                    break;
                case "MEDIUM":
                    startGame(4);
                    break;
                case "HARD":
                    startGame(5);
                    break;
            }
        }
        listening = false;
    }

    private void startGame(int diff) {
        difficulty = diff;
        playSound("YourFirstWord", yourWordIsCompletion);
        STATUS = Status.GAME;
    }

    private MediaPlayer.OnCompletionListener gameOverComplete = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
            if (score > sp.getInt("high_score", 0)) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("high_score", score);
                editor.commit();
            }
            speakScore();
            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    STATUS = Status.MAIN_MENU;
                    score = 0;
                    playSound("Welcome", null);
                }
            }.start();
        }
    };

    private MediaPlayer.OnCompletionListener yourWordIsCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(final MediaPlayer mediaPlayer) {
            /*countDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeText.setText((millisUntilFinished/1000)+"");
                    if (millisUntilFinished <= 10000 && millisUntilFinished > 1000) {
                        playSound("tick", null);
                    }
                }

                @Override
                public void onFinish() {
                    if (speaker.isSpeaking())
                        speaker.stop();
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    if (listening)
                        recognizer.stopListening();
                    playSound("GameOver", gameOverComplete);
                }
            }.start();*/
        }
    };

    private void gameWordResult(String[] words) {

        listening = false;
    }

    private MediaPlayer.OnCompletionListener correctCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            speakPrompt(correctWord);
        }
    };

    @Override
    public void onError() {
        playSound("PleaseRepeat", null);
        listening = false;
    }

    public void speakPrompt(String word) {
        mediaPlayer.play(new Sound(Sound.Type.TTS_PROMPT, word, null));
    }

    private void speakScore() {

    }

    private void initSpeaker() {

    }

    private void initSpeechRecognizer() {
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
        mediaPlayer.stopAndCancel();
        recognizer.startListening(wordIntent);

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        switch (STATUS) {
            case MAIN_MENU:
                startGame(3);
                break;
            case GAME:
                score = 0;
                break;
        }
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
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }
}
