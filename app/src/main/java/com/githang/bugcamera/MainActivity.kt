package com.githang.bugcamera

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        surface_view.setOnClickListener { startActivity(Intent(this, SurfaceViewActivity::class.java)) }
        preview.setOnClickListener { startActivity(Intent(this, PreviewActivity::class.java)) }
    }
}
