package me.ianhenry.wordladder;

import android.media.MediaPlayer;

/**
 * Created by ianhe on 5/6/2017.
 */

public class Sound {
    public static final String WELCOME = "Welcome";

    private String name;
    private MediaPlayer.OnCompletionListener listener;

    public Sound(String name) {
        this(name, null);
    }

    public Sound(String name, MediaPlayer.OnCompletionListener listener) {
        this.name = name;
        this.listener = listener;
    }

    public String getName() {
        return name;
    }

    public MediaPlayer.OnCompletionListener getListener() {
        return listener;
    }
}
