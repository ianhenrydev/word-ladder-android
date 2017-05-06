package me.ianhenry.wordladder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ianhenry.wordladder.MainActivity;
import me.ianhenry.wordladder.R;

/**
 * Created by ianhe on 5/6/2017.
 */

public class MainMenuFragment extends WordLadderFragment {
    private static final String PLAY = "PLAY";
    private static final String LEADERBOARD = "LEADERBOARD";
    private static final String TUTORIAL = "TUTORIAL";

    private MainActivity mainActivity;
    @Override
    public void onAttach(Context context) {
        mainActivity = (MainActivity)context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainActivity.playSound("Welcome", null);
        mainActivity.playSound("1stPlace", null);
    }

    @Override
    public void onSpeechResult(String[] results) {
        for (String result : results) {
            switch (result.toUpperCase()) {
                case PLAY:
                    break;
                case TUTORIAL:
                    break;
                case LEADERBOARD:
                    break;
            }
        }
    }

    @Override
    public void onDoubleTap() {
        Log.d(this.getTag(), "Double Tap");
    }
}
