/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera.callbacks;


import com.githang.bugcamera.camera.CompatCamera;

/**
 * 拍照回调接口
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2016-06-24
 * @since 2016-06-24
 */

public interface CompatPictureCallback {
    void onPictureTaken(byte[] data, CompatCamera camera) ;
}
