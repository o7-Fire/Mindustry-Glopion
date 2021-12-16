package org.o7.Fire.Glopion.Desktop.Video;


import arc.audio.Music;
import com.badlogic.gdx.video.VideoDecoder;

import java.nio.ByteBuffer;

public class VideoPlayerDesktop extends CommonVideoPlayerDesktop {
    @Override
    Music createMusic(VideoDecoder decoder, ByteBuffer audioBuffer, int audioChannels, int sampleRate) {

        return new RawAudio(decoder, audioBuffer, audioChannels, sampleRate);
    }
}