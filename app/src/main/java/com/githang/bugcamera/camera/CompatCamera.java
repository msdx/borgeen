/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera;


import com.githang.bugcamera.camera.callbacks.CameraCallback;
import com.githang.bugcamera.camera.callbacks.CompatAutoFocusCallback;
import com.githang.bugcamera.camera.callbacks.CompatPictureCallback;
import com.githang.bugcamera.camera.callbacks.CompatPreviewCallback;

/**
 * 照相机动作。
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2017-11-7 4.2.4
 * @since 2016-04-20
 */
public interface CompatCamera {
    void start();

    void stop();

    void requestFocus(CompatAutoFocusCallback callback);

    void takePicture(CompatPictureCallback raw, CompatPictureCallback jpeg);

    void setPreviewCallback(CompatPreviewCallback callback);

    void setOneshotPreviewCallback(CompatPreviewCallback callback);

    void setOnExceptionCallback(OnExceptionCallback callback);

    void setCameraCallback(CameraCallback callback);

    CameraParams.Size getPreviewSize();

    void turnOnTorch();

    void turnOffFlash();

    int getPreviewFormat();
}
