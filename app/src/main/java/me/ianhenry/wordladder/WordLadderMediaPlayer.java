package me.ianhenry.wordladder;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by ianhe on 5/6/2017.
 */

public class WordLadderMediaPlayer implements MediaPlayer.OnCompletionListener, TextToSpeech.OnInitListener {

    private MediaPlayer mediaPlayer;
    private Context context;
    private LinkedList<Sound> soundQueue;
    private Sound currentSound;
    private TextToSpeech textToSpeech;
    private boolean ready;

    public WordLadderMediaPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
        soundQueue = new LinkedList<>();
        textToSpeech = new TextToSpeech(context, this);
        ready = false;
    }

    @Override
    public void onInit(int status) {
        textToSpeech.setLanguage(Locale.US);
        textToSpeech.setSpeechRate(0.70f);
        ready = true;
        if (!mediaPlayer.isPlaying() && !textToSpeech.isSpeaking() && soundQueue.size() > 0) {
            play(soundQueue.remove());
        }
    }

    public void play(Sound sound) {
        if (mediaPlayer.isPlaying() || textToSpeech.isSpeaking() || !ready) {
            soundQueue.add(sound);
        } else {
            currentSound = sound;
            switch (currentSound.getType()) {
                case FILE:
                    playFile();
                    break;
                case TTS:
                    speakText();
                    break;
            }
        }
    }

    private void playFile() {
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd("sounds/" + currentSound.getText() + ".mp3");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void speakText() {
        textToSpeech.speak(currentSound.getText(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (currentSound.getListener() != null) {
            currentSound.getListener().onCompletion(mp);
        }
        if (soundQueue.size() > 0) {
            play(soundQueue.remove());
        }
    }
}
