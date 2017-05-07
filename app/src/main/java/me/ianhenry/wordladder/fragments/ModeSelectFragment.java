package me.ianhenry.wordladder.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ianhenry.wordladder.MainActivity;
import me.ianhenry.wordladder.R;

/**
 * Created by ianhe on 5/7/2017.
 */

public class ModeSelectFragment extends WordLadderFragment {

    private MainActivity mainActivity;
    @Override
    public void onAttach(Context context) {
        mainActivity = (MainActivity)context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mode, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainActivity.playSound("SelectDifficulty", null);
    }

    @Override
    public void onSpeechResult(String[] results) {
        for (String result : results) {
            switch (result.toUpperCase()) {
                case "EASY":
                    mainActivity.startGame(3);
                    break;
                case "MEDIUM":
                    mainActivity.startGame(4);
                    break;
                case "HARD":
                    mainActivity.startGame(5);
                    break;
            }
        }
    }

    @Override
    public void onDoubleTap() {
        mainActivity.playSound("SelectDifficulty", null);
    }
}
