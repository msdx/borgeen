/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera;

import android.os.Build;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

import com.githang.bugcamera.camera.callbacks.CameraCallback;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2017-11-7 4.2.4
 * @since 2016-04-20
 */
public class CompatCameraBuilder {

    private CameraParams mParams;
    @Nullable
    private SurfaceView mSurface;
    private CameraCallback mCameraCallback;

    public CompatCameraBuilder setCameraParams(CameraParams params) {
        mParams = params;
        return this;
    }

    public CompatCameraBuilder setSurfaceView(SurfaceView view) {
        mSurface = view;
        return this;
    }

    public CompatCameraBuilder setCameraCallback(CameraCallback callback) {
        mCameraCallback = callback;
        return this;
    }

    public CompatCamera create() {
        if (mParams == null) {
            throw new IllegalArgumentException("You must specify CameraParams with setCameraParams(CameraParams params)!");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new Camera1(mSurface, mParams, mCameraCallback);
        } else {
            // TODO: 16-4-20 待改写为支持camera2的API调用.
            return new Camera1(mSurface, mParams, mCameraCallback);
        }
    }
}
