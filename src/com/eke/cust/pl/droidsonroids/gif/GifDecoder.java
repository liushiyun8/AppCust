package com.eke.cust.pl.droidsonroids.gif;

import java.io.IOException;

import android.graphics.Bitmap;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * GifDecoder allows lightweight access to GIF frames, without wrappers like Drawable or View.
 * {@link Bitmap} with size equal to or greater than size of the GIF is needed.
 * For access only metadata (size, number of frames etc.) without pixels see {@link GifAnimationMetaData}.
 */
public class GifDecoder {

    private final GifInfoHandle mGifInfoHandle;

    /**
     * Constructs new GifDecoder
     *
     * @param inputSource source
     * @throws IOException when creation fails
     */
    public GifDecoder(final InputSource inputSource) throws IOException {
        mGifInfoHandle = inputSource.open();
    }

    /**
     * See {@link GifDrawable#getComment()}
     *
     * @return GIF comment
     */
    public String getComment() {
        return mGifInfoHandle.getComment();
    }

    /**
     * See {@link GifDrawable#getLoopCount()}
     *
     * @return loop count, 0 means that animation is infinite
     */
    public int getLoopCount() {
        return mGifInfoHandle.getLoopCount();
    }

    /**
     * See {@link GifDrawable#getInputSourceByteCount()}
     *
     * @return loop count, 0 means that animation is infinite
     */
    public long getSourceLength() {
        return mGifInfoHandle.getSourceLength();
    }

    /**
     * See {@link GifDrawable#seekTo(int)}
     *
     * @param position position to seek to in milliseconds
     * @throws IllegalArgumentException if <code>position</code>&lt;0 or <code>buffer</code> is recycled
     */
    public void seekToTime(@IntRange(from = 0, to = Integer.MAX_VALUE) final int position, @NonNull final Bitmap buffer) {
        checkBuffer(buffer);
        mGifInfoHandle.seekToTime(position, buffer);
    }

    /**
     * See {@link GifDrawable#seekToFrame(int)}
     *
     * @param frameIndex position to seek to in milliseconds
     * @throws IllegalArgumentException if <code>frameIndex</code>&lt;0 or <code>buffer</code> is recycled
     */
    public void seekToFrame(@IntRange(from = 0, to = Integer.MAX_VALUE) final int frameIndex, @NonNull final Bitmap buffer) {
        checkBuffer(buffer);
        mGifInfoHandle.seekToFrame(frameIndex, buffer);
    }

    /**
     * See {@link GifDrawable#getAllocationByteCount()}
     *
     * @return size of the allocated memory used to store pixels of this object
     */
    public long getAllocationByteCount() {
        return mGifInfoHandle.getAllocationByteCount();
    }

    /**
     * See {@link GifDrawable#getFrameDuration(int)}
     *
     * @param index index of the frame
     * @return duration of the given frame in milliseconds
     * @throws IndexOutOfBoundsException if index &lt; 0 or index &gt;= number of frames
     */
    public int getFrameDuration(@IntRange(from = 0) int index) {
        return mGifInfoHandle.getFrameDuration(index);
    }

    /**
     * See {@link GifDrawable#getDuration()}
     *
     * @return duration of of one loop the animation in milliseconds. Result is always multiple of 10.
     */
    public int getDuration() {
        return mGifInfoHandle.getDuration();
    }

    /**
     * @return width od the GIF canvas in pixels
     */
    public int getWidth() {
        return mGifInfoHandle.width;
    }

    /**
     * @return height od the GIF canvas in pixels
     */
    public int getHeight() {
        return mGifInfoHandle.height;
    }

    /**
     * @return number of frames in GIF, at least one
     */
    public int getNumberOfFrames() {
        return mGifInfoHandle.frameCount;
    }

    /**
     * @return true if GIF is animated (has at least 2 frames and positive duration), false otherwise
     */
    public boolean isAnimated() {
        return mGifInfoHandle.frameCount > 1 && getDuration() > 0;
    }

    private void checkBuffer(final Bitmap buffer) {
        if (buffer.isRecycled()) {
            throw new IllegalArgumentException("Bitmap is recycled");
        }
        if (buffer.getWidth() < mGifInfoHandle.width || buffer.getHeight() < mGifInfoHandle.height) {
            throw new IllegalArgumentException("Bitmap ia too small, size must be greater than or equal to GIF size");
        }
    }
}
