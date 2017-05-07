package me.ianhenry.wordladder.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import me.ianhenry.wordladder.MainActivity;
import me.ianhenry.wordladder.R;
import me.ianhenry.wordladder.tools.WordDatabaseHelper;

/**
 * Created by ianhe on 5/6/2017.
 */

public class GameFragment extends WordLadderFragment {
    private MainActivity mainActivity;
    private WordDatabaseHelper wordDatabaseHelper;
    private ArrayList<String> alreadyAnswered;
    private ArrayList<String> solutions;
    private int score = 0;
    private TextView promptText;
    private TextView timeText;
    private TextView scoreText;
    private TextView previousText;
    private String currentWord;

    @Override
    public void onAttach(Context context) {
        mainActivity = (MainActivity)context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        promptText = (TextView) getView().findViewById(R.id.prompt);
        timeText = (TextView) getView().findViewById(R.id.time);
        scoreText = (TextView) getView().findViewById(R.id.score);
        previousText = (TextView) getView().findViewById(R.id.previous);

        initDB();
        mainActivity.playSound("YourFirstWord", null);
        alreadyAnswered = new ArrayList<>();
        newWord();
    }

    private void initDB() {
        wordDatabaseHelper = new WordDatabaseHelper(getContext());
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

    private CountDownTimer gameTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

        }
    };

    private void newWord() {
        Cursor randoCursor = wordDatabaseHelper.getRandoWord(3);
        randoCursor.moveToFirst();
        String randoWord = randoCursor.getString(0);
        promptText.setText(randoWord.toUpperCase());
        getSolutions(randoWord);
        mainActivity.speakPrompt(randoWord);
        alreadyAnswered.add(randoWord.toUpperCase());
        currentWord = randoWord;
    }

    private void getSolutions(String word) {
        solutions = new ArrayList<>();
        Cursor cursor = wordDatabaseHelper.getSolutions(word);
        while (cursor.moveToNext()) {
            solutions.add(cursor.getString(0));
        }
    }

    @Override
    public void onSpeechResult(String[] results) {
        String debug = "";
        boolean correct = false;
        outerloop:
        for (String result : results) {
            debug += result + ",";
            for (String solution : solutions) {
                if (result.toUpperCase().equals(solution.toUpperCase())) {
                    if (!alreadyAnswered.contains(result.toUpperCase())) {
                        alreadyAnswered.add(result.toUpperCase());
                        Log.d("Correct", result);
                        promptText.setText(result.toUpperCase());
                        mainActivity.playSound("Correct", null);
                        correct = true;
                        previousText.setText(currentWord.toUpperCase() + "\n" + previousText.getText());
                        currentWord = result;
                        increaseScore();
                        getSolutions(result);
                        break outerloop;
                    }
                }
            }
        }
        if (!correct)
            mainActivity.playSound("Incorrect", null);
    }

    private void increaseScore() {
        score++;
        scoreText.setText(score+"");
    }

    @Override
    public void onDoubleTap() {
        mainActivity.speakPrompt(currentWord);
    }
}
