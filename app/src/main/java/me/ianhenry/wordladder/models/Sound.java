package me.ianhenry.wordladder.models;

import android.media.MediaPlayer;

/**
 * Created by ianhe on 5/6/2017.
 */

public class Sound {
    public enum Type {
        FILE, TTS;
    }
    public static final String WELCOME = "Welcome";

    private Type type;
    private String text;
    private MediaPlayer.OnCompletionListener listener;

    public Sound(Type type, String text) {
        this(type, text, null);
    }

    public Sound(Type type, String text, MediaPlayer.OnCompletionListener listener) {
        this.type = type;
        this.text = text;
        this.listener = listener;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public MediaPlayer.OnCompletionListener getListener() {
        return listener;
    }
}
