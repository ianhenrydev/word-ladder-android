package me.ianhenry.wordladder;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by ianhe on 2/22/2017.
 */

public class Speaker extends TextToSpeech {
    public Speaker(Context context, OnInitListener listener) {
        super(context, listener);
    }
}
