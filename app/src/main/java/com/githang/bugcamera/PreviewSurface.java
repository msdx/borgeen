package com.githang.bugcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.githang.bugcamera.camera.CameraParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2018-12-13
 * @since 2018-12-13
 */
public class PreviewSurface {
    private final ExecutorService mSerialThreadPool = Executors.newSingleThreadExecutor();
    @Nullable
    private SurfaceHolder mSurfaceHolder;
    private Rect mPreviewSize = new Rect();
    private Rect mSurfaceSize = new Rect();
    private int mPreviewFormat;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private Bitmap mBitmap;

    private final Runnable mPreviewTask = new Runnable() {
        @Override
        public void run() {
            final SurfaceHolder holder = mSurfaceHolder;
            if (holder == null || !isOpen) {
                return;
            }
            if (mFrameData != null && mPreviewFormat != 0) {
                final int width = mPreviewSize.width();
                final int height = mPreviewSize.height();
                final YuvImage yuv = new YuvImage(mFrameData, mPreviewFormat, width, height, null);
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuv.compressToJpeg(mPreviewSize, 90, out);
                final byte[] array = out.toByteArray();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inMutable = true;
                if (mBitmap != null) {
                    if (mBitmap.getWidth() == width && mBitmap.getHeight() == height
                            || Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mBitmap.getWidth() >= width && mBitmap.getHeight() >= height) {
                        options.inBitmap = mBitmap;
                    } else {
                        if (!mBitmap.isRecycled()) {
                            mBitmap.recycle();
                            mBitmap = null;
                        }
                    }
                }
                mBitmap = BitmapFactory.decodeByteArray(array, 0, array.length, options);
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Canvas canvas = holder.lockCanvas();
                canvas.drawBitmap(mBitmap, mPreviewSize, mSurfaceSize, mPaint);
                holder.unlockCanvasAndPost(canvas);
            }
            mSerialThreadPool.submit(this);
        }
    };

    private byte[] mFrameData;
    private boolean isOpen = false;

    public PreviewSurface(SurfaceView surfaceView) {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;
                if (isOpen) {
                    mSerialThreadPool.submit(mPreviewTask);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mSurfaceSize.set(0, 0, width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mSurfaceHolder = null;
            }
        });
    }

    public void startPreview() {
        isOpen = true;
        mSerialThreadPool.submit(mPreviewTask);
    }

    public void stopPreview() {
        isOpen = false;
    }

    public void destroy() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mSerialThreadPool.shutdown();
    }

    public void setPreviewSize(CameraParams.Size size) {
        mPreviewSize.set(0, 0, size.width, size.height);
    }

    public void updatePreviewData(byte[] data, int previewFormat) {
        mFrameData = data;
        mPreviewFormat = previewFormat;
    }

    @Nullable
    public byte[] takeJpegPicture() {
        if (mPreviewFormat == 0) {
            return null;
        }
        final YuvImage yuv = new YuvImage(mFrameData, mPreviewFormat, mPreviewSize.width(), mPreviewSize.height(), null);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(mPreviewSize, 90, out);
        return out.toByteArray();
    }

    @Nullable
    public Bitmap getCurrentBitmap() {
        return mBitmap;
    }
}
