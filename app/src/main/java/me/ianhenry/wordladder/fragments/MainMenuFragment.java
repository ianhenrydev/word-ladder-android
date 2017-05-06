package me.ianhenry.wordladder.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ianhenry.wordladder.R;

/**
 * Created by ianhe on 5/6/2017.
 */

public class MainMenuFragment extends WordLadderFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onSpeechResult(String[] results) {

    }

    @Override
    public void onDoubleTap() {
        Log.d(this.getTag(), "Double Tap");
    }
}
