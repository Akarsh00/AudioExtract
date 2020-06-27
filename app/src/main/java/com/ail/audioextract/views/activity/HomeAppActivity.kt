package com.ail.audioextract.views.activity

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ail.audioextract.R
import com.ail.audioextract.accessmedia.BaseMainViewModel

class HomeAppActivity : AppCompatActivity() {
    lateinit var appBaseViewModel: BaseMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_converter_main)
        appBaseViewModel = ViewModelProvider(this).get(BaseMainViewModel::class.java)
        if (checkStoragePermissionGrantedOrNot() == PackageManager.PERMISSION_GRANTED) {
            appBaseViewModel.queryListVideosFromBucket(applicationContext, null)
        }
    }
    private fun checkStoragePermissionGrantedOrNot(): Int {
        val pm = applicationContext.packageManager
        val hasPerm = pm.checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                applicationContext.packageName)
        return hasPerm
    }

}