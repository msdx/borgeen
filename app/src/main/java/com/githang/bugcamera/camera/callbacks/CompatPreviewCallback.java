/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera.callbacks;

import com.githang.bugcamera.camera.CompatCamera;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2017-11-07 4.2.4
 * @since 2017-11-07 4.2.4
 */
public interface CompatPreviewCallback {
    void onPreviewFrame(byte[] data, CompatCamera camera);
}
