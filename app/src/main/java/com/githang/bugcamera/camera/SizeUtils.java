/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.bugcamera.camera;

import android.hardware.Camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 尺寸计算工具
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2017-11-7 4.2.4
 * @since 2016-04-20
 */
@SuppressWarnings("deprecation")
public class SizeUtils {
    public static final Comparator<Camera.Size> DESC = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size a, Camera.Size b) {
            return b.height * b.width - a.height * a.width;
        }
    };

    public static final Comparator<Camera.Size> ASC = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size a, Camera.Size b) {
            return a.height * a.width - b.height * b.width;
        }
    };

    /**
     * 找到最佳大小
     *
     * @param sizes       支持的尺寸列表
     * @param defaultSize 默认大小
     * @param matcher     判断是否匹配条件
     * @return 返回计算之后的尺寸
     */
    public static Camera.Size findSize(List<Camera.Size> sizes, Camera.Size defaultSize, SizeMatcher matcher) {
        Iterator<Camera.Size> it = sizes.iterator();
        Camera.Size size;
        int match;
        while (it.hasNext()) {
            size = it.next();
            //移除不满足比例的尺寸
            match = matcher.match(size.width, size.height);
            if (match < 0) {
                it.remove();
            } else if (match == 0) {
                return size;
            }
        }

        // 返回符合条件中最大尺寸的一个
        if (!sizes.isEmpty()) {
            return sizes.get(0);
        }
        // 没得选，默认吧
        return defaultSize;
    }

    public static void sortSizes(List<Camera.Size> sizes, Comparator<Camera.Size> comparator) {
        Collections.sort(sizes, comparator);
    }
}
