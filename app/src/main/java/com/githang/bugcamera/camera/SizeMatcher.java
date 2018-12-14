/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera;

/**
 * 比较接口
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2017-11-7 4.2.4
 * @since 2016-04-20
 */
public interface SizeMatcher {
    /**
     * 判断当前尺寸是否匹配
     * @param width 尺寸宽
     * @param height 尺寸高
     * @return 小于0表示不匹配，等于0表示精准匹配，大于0表示可选
     */
    int match(int width, int height);
}
