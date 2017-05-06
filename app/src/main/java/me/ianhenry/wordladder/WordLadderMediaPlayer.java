package me.ianhenry.wordladder;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ianhe on 5/6/2017.
 */

public class WordLadderMediaPlayer implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private Context context;
    private LinkedList<Sound> soundQueue;
    private Sound currentSound;

    public WordLadderMediaPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
        soundQueue = new LinkedList<>();
    }

    public void play(Sound sound) {
        if (mediaPlayer.isPlaying()) {
            soundQueue.add(sound);
        } else {
            currentSound = sound;
            try {
                AssetFileDescriptor descriptor = context.getAssets().openFd("sounds/" + currentSound.getName() + ".mp3");
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
