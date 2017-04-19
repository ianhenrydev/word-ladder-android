package me.ianhenry.wordladder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import me.ianhenry.wordladder.events.WordResultListener;

public class MainActivity extends AppCompatActivity implements WordResultListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

    private SpeechRecognizer recognizer;
    private final int PERMISSION_REQUEST_AUDIO = 26;
    private Intent wordIntent;
    private TextView textView;
    private TextView debugText;
    private TextView scoreText;
    private RelativeLayout layout;
    private Boolean listening = false;
    private TextToSpeech speaker;
    private WordDatabaseHelper wordDatabaseHelper;
    private Cursor resultCursor;
    private int score;
    private ArrayList<String> solutions;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSpeaker();

        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);

        layout = (RelativeLayout)findViewById(R.id.activity_main);
        textView = (TextView)findViewById(R.id.textView);
        debugText = (TextView)findViewById(R.id.debugText);
        scoreText = (TextView)findViewById(R.id.scoreText);

        initDB();

        score = 0;
    }

    private void newWord() {
        Cursor randoCursor = wordDatabaseHelper.getRandoWord(3);
        randoCursor.moveToFirst();
        String randoWord = randoCursor.getString(0);
        textView.setText(randoWord.toUpperCase());
        getSolutions(randoWord);
        speakPrompt(randoWord);
    }

    private void initDB() {
        wordDatabaseHelper = new WordDatabaseHelper(this);
        try {

            wordDatabaseHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            wordDatabaseHelper.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;

        }
    }

    private void getSolutions(String word) {
        resultCursor = wordDatabaseHelper.getSolutions(word);
        Log.d("Num results", resultCursor.getCount()+"");
        while (resultCursor.moveToNext()) {
            Log.d("Num results", resultCursor.getString(0));
        }
    }

    private void increaseScore() {
        score++;
        scoreText.setText(""+score);
    }

    @Override
    public void onWordResult(String word) {
        debugText.setText(word.toUpperCase());
        resultCursor.moveToFirst();
        while (resultCursor.moveToNext()) {
            if (word.toUpperCase().equals(resultCursor.getString(0))) {
                Log.d("Correct", word);
                speakPrompt(word);
                increaseScore();
                textView.setText(word.toUpperCase());
                getSolutions(word);
            }
        }
        listening = false;
    }

    @Override
    public void onError() {
        debugText.setText("error");
        listening = false;
    }

    private void speakPrompt(String word) {
        String longWord = word.replace("", "... ");
        speaker.speak(word + ": " + longWord, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void initSpeaker() {
        speaker = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                speaker.setLanguage(Locale.US);
                speaker.setSpeechRate(0.70f);
                newWord();
                checkForPermissions();
            }
        });
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
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            //if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            //} else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_AUDIO);
            //}
        } else {
            initSpeechRecognizer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_AUDIO: {
                // If request is cancelled, the result arrays are empty.
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
            recognizer.startListening(wordIntent);
            listening = true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        newWord();
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
