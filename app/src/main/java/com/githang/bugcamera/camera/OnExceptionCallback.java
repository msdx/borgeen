/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera;

import android.app.Activity;

import com.githang.bugcamera.R;

/**
 * 相机异常回调
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2017-12-11 4.4
 * @since 2016-04-22
 */
public interface OnExceptionCallback {
    /**
     * 打开相机失败
     *
     * @param e                     异常信息
     * @param maybePermissionDenied 是否可能是权限问题
     */
    void onOpenFailed(java.lang.Exception e, boolean maybePermissionDenied);


    class SimpleCameraExceptionCallback implements OnExceptionCallback {
        private final Activity mActivity;

        public SimpleCameraExceptionCallback(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onOpenFailed(java.lang.Exception e, boolean maybePermissionDenied) {
            mActivity.finish();
        }
    }
}
