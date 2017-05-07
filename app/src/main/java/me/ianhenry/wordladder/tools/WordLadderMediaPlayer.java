package me.ianhenry.wordladder.tools;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

import me.ianhenry.wordladder.models.Sound;

/**
 * Created by ianhe on 5/6/2017.
 */

public class WordLadderMediaPlayer implements MediaPlayer.OnCompletionListener, TextToSpeech.OnInitListener {

    private MediaPlayer mediaPlayer;
    private MediaPlayer backgroundMediaPlayer;
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
        textToSpeech.setSpeechRate(1f);
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
                case TTS_PROMPT:
                    speakPrompt();
                    break;
            }
        }
    }

    public void playBackground(String name) {
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd("sounds/" + name + ".mp3");
            backgroundMediaPlayer = new MediaPlayer();
            backgroundMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            backgroundMediaPlayer.prepare();
            backgroundMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void speakPrompt() {
        String word = currentSound.getText();
        textToSpeech.speak(word.toLowerCase(), TextToSpeech.QUEUE_ADD, null, null);
        for (int i = 0; i < word.length(); i ++) {
            textToSpeech.playSilentUtterance(100, TextToSpeech.QUEUE_ADD, null);
            textToSpeech.speak(word.charAt(i)+"", TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    public void stopAndCancel() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        while (soundQueue.size() > 0) {
            soundQueue.remove();
        }
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
