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
class SurfaceViewActivity : AppCompatActivity() {
    lateinit var camera: CompatCamera
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val cameraParams = CameraParams()
                .setAutoFindBestSize(true)
                .setFocusMode(CameraParams.FOCUS_MODE_CONTINUOUS_VIDEO)
                .setOnlyPreview(false)
        camera = CompatCameraBuilder()
                .setSurfaceView(surface_view)
                .setCameraParams(cameraParams)
                .setCameraCallback(object : CameraCallback {
                    override fun onCameraOpened(camera: CompatCamera) {
                        Toast.makeText(this@SurfaceViewActivity, "相机打开", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@SurfaceViewActivity, "预览大小" + camera.previewSize.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onCameraClosed() {
                        Toast.makeText(this@SurfaceViewActivity, "相机关闭", Toast.LENGTH_SHORT).show()
                    }
                })
                .create()
        camera.setOnExceptionCallback { e, _ ->
            Toast.makeText(this, e.message + e.toString(), Toast.LENGTH_SHORT).show()
        }
        take_photo.setOnClickListener {
            camera.takePicture(null) { data, _ ->
                try {
                    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "picture.jpg")
                    val fos = FileOutputStream(file)
                    fos.write(data)
                    fos.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                startActivity(Intent(this, PictureActivity::class.java))
            }
        }
    }

    override fun onPause() {
        camera.stop()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        camera.start()
    }
}
