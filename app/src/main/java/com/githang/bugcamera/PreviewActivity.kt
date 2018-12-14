package com.githang.bugcamera

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.githang.bugcamera.camera.CameraParams
import com.githang.bugcamera.camera.CompatCamera
import com.githang.bugcamera.camera.CompatCameraBuilder
import com.githang.bugcamera.camera.callbacks.CameraCallback
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileOutputStream

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @version 2018-12-13
 * @since 2018-12-13
 */
class PreviewActivity : AppCompatActivity() {

    lateinit var camera: CompatCamera
    lateinit var previewSurface: PreviewSurface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        previewSurface = PreviewSurface(surface_view)
        val cameraParams = CameraParams()
                .setAutoFindBestSize(true)
                .setFocusMode(CameraParams.FOCUS_MODE_CONTINUOUS_VIDEO)
                .setOnlyPreview(true)
        camera = CompatCameraBuilder()
                .setCameraParams(cameraParams)
                .setCameraCallback(object : CameraCallback {
                    override fun onCameraOpened(camera: CompatCamera) {
                        previewSurface.startPreview()
                        previewSurface.setPreviewSize(camera.previewSize)
                        Toast.makeText(this@PreviewActivity, "相机打开", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@PreviewActivity, "预览大小" + camera.previewSize.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onCameraClosed() {
                        previewSurface.stopPreview()
                        Toast.makeText(this@PreviewActivity, "相机关闭", Toast.LENGTH_SHORT).show()
                    }
                })
                .create()
        camera.setOnExceptionCallback { e, maybePermissionDenied ->
            Toast.makeText(this, "发生异常" + e.message + e.toString(), Toast.LENGTH_SHORT).show()
        }
        take_photo.setOnClickListener {
            val byte = previewSurface.takeJpegPicture() ?: return@setOnClickListener
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "picture.jpg")
            val fos = FileOutputStream(file)
            fos.write(byte)
            fos.flush()
            startActivity(Intent(this, PictureActivity::class.java))
        }
    }


    override fun onPause() {
        camera.setPreviewCallback(null)
        camera.stop()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        camera.setPreviewCallback { data, camera ->
            previewSurface.updatePreviewData(data, camera.previewFormat)
        }
        camera.start()
    }

    override fun onDestroy() {
        previewSurface.destroy()
        super.onDestroy()
    }
}