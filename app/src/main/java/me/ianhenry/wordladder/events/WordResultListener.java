package me.ianhenry.wordladder.events;

/**
 * Created by ianhe on 2/22/2017.
 */

public interface WordResultListener {
    void onWordResult(String[] words);
    void onError();
}
