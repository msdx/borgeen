/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera;

/**
 * 相机参数
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2016-04-20 4.2.4
 * @since 2016-04-20
 */
public class CameraParams {
    public static final String FOCUS_MODE_AUTO = Camera.Parameters.FOCUS_MODE_AUTO;
    public static final String FOCUS_MODE_CONTINUOUS_VIDEO = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;

    private int mPictureFormat = ImageFormat.JPEG;
    private int mRotation;
    private String mFocusMode;
    private boolean mAutoFindBestSize;
    private int mMinPixels;
    private boolean mAdjustSurfaceViewSize;
    private boolean mOnlyPreview;

    public int getPictureFormat() {
        return mPictureFormat;
    }

    public CameraParams setPictureFormat(int pictureFormat) {
        mPictureFormat = pictureFormat;
        return this;
    }

    public int getRotation() {
        return mRotation;
    }

    public CameraParams setRotation(int rotation) {
        mRotation = rotation;
        return this;
    }

    public String getFocusMode() {
        return mFocusMode;
    }

    public CameraParams setFocusMode(String focusMode) {
        mFocusMode = focusMode;
        return this;
    }

    public boolean isAutoFindBestSize() {
        return mAutoFindBestSize;
    }

    public CameraParams setAutoFindBestSize(boolean autoFindBestSize) {
        mAutoFindBestSize = autoFindBestSize;
        return this;
    }

    public int getMinPixels() {
        return mMinPixels;
    }

    /**
     * 照片的最低像素
     * @param minPixels 最低像素
     * @return
     */
    public CameraParams setMinPixels(int minPixels) {
        mMinPixels = minPixels;
        return this;
    }

    public boolean isAdjustSurfaceViewSize() {
        return mAdjustSurfaceViewSize;
    }

    /**
     * 设置是否自适应SurfaceView大小。当SurfaceView看起来变形时，你可能会需要这个参数。
     * @param adjustSurfaceViewSize 是否自适应SurfaceView大小。
     * @return
     */
    public CameraParams setAdjustSurfaceViewSize(boolean adjustSurfaceViewSize) {
        mAdjustSurfaceViewSize = adjustSurfaceViewSize;
        return this;
    }

    public boolean isOnlyPreview() {
        return mOnlyPreview;
    }

    /**
     * 设置是否只预览
     * @param onlyPreview true为只预览
     */
    public CameraParams setOnlyPreview(boolean onlyPreview) {
        mOnlyPreview = onlyPreview;
        return this;
    }

    public static class Size {
        public int width;
        public int height;

        @Override
        public String toString() {
            return "Size{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
