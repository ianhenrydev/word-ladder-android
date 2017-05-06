package me.ianhenry.wordladder.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by ianhe on 5/6/2017.
 */

public abstract class WordLadderFragment extends Fragment {
    public abstract void onSpeechResult(String[] results);
    public abstract void onDoubleTap();
}
