package org.o7.Fire.Glopion.Desktop.Video;

import arc.audio.Music;
import arc.files.Fi;
import com.badlogic.gdx.video.VideoDecoder;

import java.nio.ByteBuffer;

public class RawAudio extends Music {
    VideoDecoder decoder;
    ByteBuffer buffer;
    int sampleRate;
    int channels;

    public RawAudio(VideoDecoder decoder, ByteBuffer audioBuffer, int audioChannels, int sampleRate) {
        this.decoder = decoder;
        this.buffer = audioBuffer;
        this.sampleRate = sampleRate;
        this.channels = audioChannels;
        Fi temp = Fi.tempFile(decoder.hashCode() + ".wav");
        decoder.writeAudio(temp);
    }

    public void load() {
        if (handle != 0) return;


    }


}
