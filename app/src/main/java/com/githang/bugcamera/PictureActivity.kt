package com.githang.bugcamera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import java.io.File

/**
 * @author 黄浩杭 (msdx.android@qq.com)
 * @since 2018-12-13
 */
class PictureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        setContentView(imageView)
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "picture.jpg")
        val bitmap = BitmapFactory.decodeFile(file.path)
        imageView.setImageBitmap(bitmap)
    }
}
