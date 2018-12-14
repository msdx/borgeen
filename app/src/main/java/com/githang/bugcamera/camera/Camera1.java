/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera;

import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.githang.bugcamera.camera.callbacks.CameraCallback;
import com.githang.bugcamera.camera.callbacks.CompatAutoFocusCallback;
import com.githang.bugcamera.camera.callbacks.CompatPictureCallback;
import com.githang.bugcamera.camera.callbacks.CompatPreviewCallback;

import java.io.IOException;
import java.util.List;

/**
 * 对Camera的封装
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2017-12-11 4.4
 * @since 2016-04-20
 */
@SuppressWarnings("deprecation")
public final class Camera1 implements CompatCamera, SurfaceHolder.Callback {
    @Nullable
    private Camera mCamera;

    @Nullable
    private SurfaceView mSurfaceView;
    private CameraParams mCameraParams;

    private boolean mSurfacePreview;
    private boolean mIsPortrait;
    private boolean mWaitForTakePhoto;
    private boolean mIsSurfaceReady;

    private Camera.Size mBestPictureSize;
    private Camera.Size mBestPreviewSize;

    private OnExceptionCallback mExceptionCallback;

    private Camera.PictureCallback mRawCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            CompatPictureCallback callback = mCompatRawCallback;
            if (callback != null) {
                callback.onPictureTaken(data, Camera1.this);
            }
        }
    };
    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            CompatPictureCallback callback = mCompatJpegCallback;
            if (callback != null) {
                callback.onPictureTaken(data, Camera1.this);
            }
            mWaitForTakePhoto = false;
        }
    };
    private CompatPictureCallback mCompatRawCallback;
    private CompatPictureCallback mCompatJpegCallback;

    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            CompatAutoFocusCallback callback = mCompatAutoFocusCallback;
            if (callback != null) {
                callback.onAutoFocus(success, Camera1.this);
            }
        }
    };
    private CompatAutoFocusCallback mCompatAutoFocusCallback;

    private CompatPreviewCallback mCompatPreviewCallback;
    private boolean mOneshot = false;

    private CameraParams.Size mPreviewSize = new CameraParams.Size();

    private CameraCallback mCameraCallback;

    Camera1(@Nullable SurfaceView view, CameraParams params, CameraCallback cameraCallback) {
        mSurfaceView = view;
        mCameraParams = params;
        mCameraCallback = cameraCallback;

        if (mSurfaceView != null) {
            mSurfaceView.getHolder().addCallback(this);
            mSurfacePreview = true;
        }

        final int rotation = params.getRotation();
        mIsPortrait = (rotation == 90 || rotation == 270);
    }

    @Override
    public void start() {
        if (!mSurfacePreview) {
            open();
        } else {
            mSurfaceView.post(new Runnable() {
                @Override
                public void run() {
                    open();
                }
            });
        }
    }

    private void open() {
        try {
            if (mCamera == null) {
                mCamera = Camera.open();
            }

            if (mCamera == null) {
                throw new RuntimeException("Failed to connect");
            }

            final Camera.Parameters cameraParams = mCamera.getParameters();
            cameraParams.setPictureFormat(mCameraParams.getPictureFormat());
            cameraParams.setRotation(mCameraParams.getRotation());
            if (mCameraParams.getFocusMode() != null) {
                cameraParams.setFocusMode(mCameraParams.getFocusMode());
            }

            // 长边比短边
            final float maxRatio;
            if (mSurfacePreview) {
                maxRatio = mIsPortrait ? mSurfaceView.getHeight() * 1f / mSurfaceView.getWidth() :
                        mSurfaceView.getWidth() * 1f / mSurfaceView.getHeight();
            } else {
                maxRatio = Integer.MAX_VALUE;
            }

            final Camera.Size previewSize;
            if (!mCameraParams.isOnlyPreview()) {
                final Camera.Size pictureSize = setPictureSize(cameraParams, maxRatio);
                previewSize = setPreviewSize(cameraParams, pictureSize, maxRatio);
            } else {
                previewSize = setPreviewSize(cameraParams);
            }

            mPreviewSize.width = previewSize.width;
            mPreviewSize.height = previewSize.height;

            if (mSurfacePreview && mCameraParams.isAdjustSurfaceViewSize()) {
                setSurfaceViewSize(previewSize);
            }
            try {
                mCamera.setParameters(cameraParams);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mIsSurfaceReady || !mSurfacePreview) {
                startPreview();
            }
            onCameraOpened();
        } catch (Exception e) {
            if (mExceptionCallback != null) {
                mExceptionCallback.onOpenFailed(e, true);
            }
        }
    }

    private void onCameraOpened() {
        if (mCameraCallback != null) {
            mCameraCallback.onCameraOpened(this);
        }
    }

    private Camera.Size setPictureSize(Camera.Parameters cameraParams, final float maxRatio) {
        Camera.Size pictureSize = cameraParams.getPictureSize();
        if (mCameraParams.isAutoFindBestSize()) {
            if (mBestPictureSize == null) {
                final int minPixels = mCameraParams.getMinPixels();
                final List<Camera.Size> sizeList = cameraParams.getSupportedPictureSizes();
                SizeUtils.sortSizes(sizeList, SizeUtils.DESC);
                pictureSize = SizeUtils.findSize(sizeList,
                        pictureSize,
                        new SizeMatcher() {
                            @Override
                            public int match(int width, int height) {
                                return (width > maxRatio * height || width * height < minPixels) ? -1 : 1;
                            }
                        });
                mBestPictureSize = pictureSize;
            } else {
                pictureSize = mBestPictureSize;
            }
            cameraParams.setPictureSize(pictureSize.width, pictureSize.height);
        }
        return pictureSize;

    }

    private Camera.Size setPreviewSize(Camera.Parameters cameraParams, Camera.Size pictureSize, final float maxRatio) {
        final int pWidth = pictureSize.width;
        final int pHeight = pictureSize.height;
        final boolean findBestPictureSize = pWidth / pHeight < maxRatio;
        Camera.Size previewSize = cameraParams.getPreviewSize();
        if (mCameraParams.isAutoFindBestSize()) {
            if (mBestPreviewSize == null) {
                final List<Camera.Size> sizeList = cameraParams.getSupportedPreviewSizes();
                SizeUtils.sortSizes(sizeList, SizeUtils.DESC);
                previewSize = SizeUtils.findSize(sizeList, previewSize, new SizeMatcher() {
                    @Override
                    public int match(int width, int height) {
                        if (width > maxRatio * height) {
                            return -1;
                        } else if (findBestPictureSize && width * pHeight == height * pWidth) {
                            return 0;
                        }
                        return 1;
                    }
                });
                mBestPreviewSize = previewSize;
            } else {
                previewSize = mBestPreviewSize;
            }
        }
        cameraParams.setPreviewSize(previewSize.width, previewSize.height);
        return previewSize;
    }

    private Camera.Size setPreviewSize(Camera.Parameters cameraParams) {
        Camera.Size previewSize = cameraParams.getPreviewSize();
        if (mCameraParams.isAutoFindBestSize()) {
            if (mBestPreviewSize == null) {
                final List<Camera.Size> sizeList = cameraParams.getSupportedPreviewSizes();
                SizeUtils.sortSizes(sizeList, SizeUtils.DESC);
                if (mSurfacePreview) {
                    final int tWidth;
                    final int tHeight;
                    if (mIsPortrait) {
                        tWidth = mSurfaceView.getHeight();
                        tHeight = mSurfaceView.getWidth();
                    } else {
                        tWidth = mSurfaceView.getWidth();
                        tHeight = mSurfaceView.getHeight();
                    }
                    previewSize = SizeUtils.findSize(sizeList, previewSize, new SizeMatcher() {
                        @Override
                        public int match(int width, int height) {
                            if (width < tWidth || tHeight < height) {
                                return -1;
                            } else if (width * tHeight == height * tWidth) {
                                return 0;
                            }
                            return 1;
                        }
                    });
                }
                mBestPreviewSize = previewSize;
            } else {
                previewSize = mBestPreviewSize;
            }
        }
        cameraParams.setPreviewSize(previewSize.width, previewSize.height);
        return previewSize;
    }

    private void setSurfaceViewSize(Camera.Size size) {
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        if (mIsPortrait) {
            params.height = mSurfaceView.getWidth() * size.width / size.height;
        } else {
            params.width = mSurfaceView.getHeight() * size.width / size.height;
        }
        mSurfaceView.setLayoutParams(params);
    }

    private void startPreview() {
        if (mCamera == null) {
            return;
        }
        try {
            if (mSurfacePreview) {
                mCamera.setPreviewDisplay(mSurfaceView.getHolder());
            }
            mCamera.setDisplayOrientation(mCameraParams.getRotation());
            mCamera.startPreview();
            setPreviewCallbackInternal(mCompatPreviewCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (mCamera == null) {
            onCameraClosed();
            return;
        }
        try {
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.release();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            mCamera = null;
            onCameraClosed();
        }
    }

    private void onCameraClosed() {
        if (mCameraCallback != null) {
            mCameraCallback.onCameraClosed();
        }
    }

    @Override
    public void requestFocus(CompatAutoFocusCallback callback) {
        if (mCamera == null || mWaitForTakePhoto) {
            return;
        }
        mCompatAutoFocusCallback = callback;
        mCamera.autoFocus(mAutoFocusCallback);
    }

    @Override
    public void takePicture(final CompatPictureCallback raw, CompatPictureCallback jpeg) {
        if (mCamera == null || mWaitForTakePhoto) {
            return;
        }
        if (jpeg != null) {
            mWaitForTakePhoto = true;
        }
        mCompatRawCallback = raw;
        mCompatJpegCallback = jpeg;

        mCamera.takePicture(null,
                raw == null ? null : mRawCallback,
                jpeg == null ? null : mJpegCallback);
    }

    @Override
    public void setPreviewCallback(final CompatPreviewCallback callback) {
        mCompatPreviewCallback = callback;
        mOneshot = false;
        setPreviewCallbackInternal(callback);
    }

    @Override
    public void setOneshotPreviewCallback(CompatPreviewCallback callback) {
        mCompatPreviewCallback = callback;
        mOneshot = true;
        setPreviewCallbackInternal(callback);
    }

    private void setPreviewCallbackInternal(final CompatPreviewCallback callback) {
        if (mCamera != null) {
            if (callback == null) {
                mCamera.setPreviewCallback(null);
            } else {
                Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        callback.onPreviewFrame(data, Camera1.this);
                    }
                };
                if (mOneshot) {
                    mCamera.setOneShotPreviewCallback(previewCallback);
                } else {
                    mCamera.setPreviewCallback(previewCallback);
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsSurfaceReady = true;
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsSurfaceReady = false;
    }

    @Override
    public void setOnExceptionCallback(OnExceptionCallback callback) {
        mExceptionCallback = callback;
    }

    @Override
    public void setCameraCallback(CameraCallback callback) {
        mCameraCallback = callback;
    }

    @Override
    public CameraParams.Size getPreviewSize() {
        return mPreviewSize;
    }

    @Override
    public void turnOnTorch() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters params = mCamera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(params);
    }

    @Override
    public void turnOffFlash() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters params = mCamera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(params);
    }

    @Override
    public int getPreviewFormat() {
        return mCamera == null ? 0 : mCamera.getParameters().getPreviewFormat();
    }
}
