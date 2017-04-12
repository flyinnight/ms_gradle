package com.ov.omniwificam;

import android.util.Log;
import android.R.integer;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Aout {
    /**
     * Java side of the audio output module for Android.
     * Uses an AudioTrack to play decoded audio buffers.
     *
     */

    private AudioTrack mAudioTrack;
    private static final String TAG = "ov780wifi";

    public void init(int sampleRateInHz, int channels, int samples) {
        Log.d(TAG, sampleRateInHz + ", " + channels + ", " + samples + "=>" + channels*samples);
        int bufSize = AudioTrack.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                     sampleRateInHz,
                                     AudioFormat.CHANNEL_CONFIGURATION_MONO, //STEREO,
                                     AudioFormat.ENCODING_PCM_16BIT,
                                     bufSize,
                                     AudioTrack.MODE_STREAM);
        
    }

    public void release() {
        Log.d(TAG, "Stopping audio playback");
        // mAudioTrack.stop();
        mAudioTrack.release();
        mAudioTrack = null;
    }

    public void playBuffer(byte[] audioData, int bufferSize, int nbSamples) {
//        Log.d(TAG, "Buffer size: " + bufferSize + " nb samples: " + nbSamples);
        if (mAudioTrack.write(audioData, 0, bufferSize) != bufferSize)
        {
            Log.w(TAG, "Could not write all the samples to the audio device");
        }
        mAudioTrack.play();
    }
}
