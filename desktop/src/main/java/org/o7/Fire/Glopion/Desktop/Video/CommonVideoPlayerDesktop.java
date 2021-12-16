package org.o7.Fire.Glopion.Desktop.Video;


import arc.Core;
import arc.audio.Music;
import arc.files.Fi;
import arc.graphics.GL20;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.gl.PixmapTextureData;
import com.badlogic.gdx.video.FfMpeg;
import com.badlogic.gdx.video.VideoDecoder;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Desktop implementation of the VideoPlayer
 *
 * @author Rob Bogie rob.bogie@codepoke.net
 */
abstract public class CommonVideoPlayerDesktop implements VideoPlayer {
    VideoDecoder decoder;
    Texture texture;
    Music audio;
    long startTime = 0;
    boolean showAlreadyDecodedFrame = false;

    BufferedInputStream inputStream;
    ReadableByteChannel fileChannel;

    boolean paused = false;
    long timeBeforePause = 0;

    int currentVideoWidth, currentVideoHeight;
    VideoSizeListener sizeListener;
    CompletionListener completionListener;
    Fi currentFile;

    boolean playing = false;

    public CommonVideoPlayerDesktop() {
    }

    abstract Music createMusic(VideoDecoder decoder, ByteBuffer audioBuffer, int audioChannels, int sampleRate);

    @Override
    public boolean play(Fi file) throws FileNotFoundException {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            throw new FileNotFoundException("Could not find file: " + file.path());
        }

        currentFile = file;

        if (!FfMpeg.isLoaded()) {
            FfMpeg.loadLibraries();
        }

        if (decoder != null) {
            // Do all the cleanup
            stop();
        }

        inputStream = file.read(1024 * 1024);
        fileChannel = Channels.newChannel(inputStream);

        decoder = new VideoDecoder();
        VideoDecoder.VideoDecoderBuffers buffers;
        try {
            buffers = decoder.loadStream(this, "readFileContents");

            if (buffers != null) {
                ByteBuffer audioBuffer = buffers.getAudioBuffer();
                if (audioBuffer != null) {
                    if (audio != null) audio.dispose();
                    audio = createMusic(decoder, audioBuffer, buffers.getAudioChannels(), buffers.getAudioSampleRate());
                }
                currentVideoWidth = buffers.getVideoWidth();
                currentVideoHeight = buffers.getVideoHeight();
                if (texture != null && (texture.width != currentVideoWidth || texture.height != currentVideoHeight)) {
                    texture.dispose();
                    texture = null;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (sizeListener != null) {
            sizeListener.onVideoSize(currentVideoWidth, currentVideoHeight);
        }

        playing = true;
        return true;
    }

    /**
     * Called by jni to fill in the file buffer.
     *
     * @param buffer The buffer that needs to be filled
     * @return The amount that has been filled into the buffer.
     */
    @SuppressWarnings("unused")
    private int readFileContents(ByteBuffer buffer) {
        try {
            buffer.rewind();
            return fileChannel.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean update() {
        if (decoder != null && !paused && playing) {
            if (startTime == 0) {
                // Since startTime is 0, this means that we should now display the first frame of the video, and set the
                // time.
                startTime = System.currentTimeMillis();
                if (audio != null) {
                    audio.play();
                }
            }

            boolean newFrame = false;
            if (!showAlreadyDecodedFrame) {
                ByteBuffer videoData = decoder.nextVideoFrame();
                if (videoData != null) {
                    if (texture == null)
                        texture = new Texture(new PixmapTextureData(new Pixmap(currentVideoWidth, currentVideoHeight), false, false));
                    texture.bind();
                    Core.gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGB, currentVideoWidth, currentVideoHeight, 0, GL20.GL_RGB,
                            GL20.GL_UNSIGNED_BYTE, videoData);
                    newFrame = true;
                } else {
                    playing = false;
                    if (completionListener != null) {
                        completionListener.onCompletionListener(currentFile);
                    }
                    return false;
                }
            }

            showAlreadyDecodedFrame = false;
            long currentFrameTimestamp = (long) (decoder.getCurrentFrameTimestamp() * 1000);
            long currentVideoTime = (System.currentTimeMillis() - startTime);
            int difference = (int) (currentFrameTimestamp - currentVideoTime);
            if (difference > 20) {
                // Difference is more than a frame, draw this one twice
                showAlreadyDecodedFrame = true;
            }
            return newFrame;
        }
        return false;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    /**
     * Will return whether the buffer is filled. At the time of writing, the buffer used can store 10 frames of video. You can
     * find the value in jni/VideoDecoder.h
     *
     * @return whether buffer is filled.
     */
    @Override
    public boolean isBuffered() {
        if (decoder != null) {
            return decoder.isBuffered();
        }
        return false;
    }

    @Override
    public void stop() {
        playing = false;

        if (audio != null) {
            audio.dispose();
            audio = null;
        }
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
        if (decoder != null) {
            decoder.dispose();
            decoder = null;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }

        startTime = 0;
        showAlreadyDecodedFrame = false;
    }

    @Override
    public void pause() {
        if (!paused) {
            paused = true;
            if (audio != null) {
                audio.pause(true);
            }
            timeBeforePause = System.currentTimeMillis() - startTime;
        }
    }

    @Override
    public void resume() {
        if (paused) {
            paused = false;
            if (audio != null) {
                audio.pause(false);
            }
            startTime = System.currentTimeMillis() - timeBeforePause;
        }
    }

    @Override
    public void dispose() {
        stop();
    }

    @Override
    public void setOnVideoSizeListener(VideoSizeListener listener) {
        sizeListener = listener;
    }

    @Override
    public void setOnCompletionListener(CompletionListener listener) {
        completionListener = listener;
    }

    @Override
    public int getVideoWidth() {
        return currentVideoWidth;
    }

    @Override
    public int getVideoHeight() {
        return currentVideoHeight;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public float getVolume() {
        return audio.getVolume();
    }

    @Override
    public void setVolume(float volume) {
        audio.setVolume(volume);
    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public void setLooping(boolean looping) {
        // TODO
    }

    @Override
    public int getCurrentTimestamp() {
        return (int) (decoder.getCurrentFrameTimestamp() * 1000);
    }

}
