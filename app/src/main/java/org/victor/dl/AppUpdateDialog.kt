package org.victor.dl

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.victor.dl.library.CoroutinesDL.download
import org.victor.dl.library.core.DownloadTask
import org.victor.dl.library.data.State
import org.victor.dl.library.util.InstallApkUtil
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AppUpdateDialog
 * Author: Victor
 * Date: 2021/6/2 17:20
 * Description: 
 * -----------------------------------------------------------------
 */
class AppUpdateDialog(context: Context): AbsDialog(context), View.OnClickListener {

    val TAG = "AppUpdateDialog"

    var mIvClose: ImageView? = null
    var mTvUpdateNow: MaterialTextView? = null
    var mTvNewVersion: MaterialTextView? = null
    var mTvUpdateContent: MaterialTextView? = null
    var mTvStatus: MaterialTextView? = null
    var mPbDownloadProgress: ProgressBar? = null

    var lifecycleScope: CoroutineScope? = null
    var mLatestVersionData: LatestVersionData? = null
    var downloadTask: DownloadTask? = null
    override fun bindContentView() = R.layout.dlg_app_update

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 1.0).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize() {
        mIvClose = findViewById(R.id.mIvClose)
        mTvUpdateNow = findViewById(R.id.mTvUpdateNow)
        mTvNewVersion = findViewById(R.id.mTvNewVersion)
        mTvUpdateContent = findViewById(R.id.mTvUpdateContent)
        mTvStatus = findViewById(R.id.mTvStatus)
        mPbDownloadProgress = findViewById(R.id.mPbDownloadProgress)

        mIvClose?.setOnClickListener(this)
        mTvUpdateNow?.setOnClickListener(this)
    }

    fun initData() {
        mTvNewVersion?.text = mLatestVersionData?.versionName
        mTvUpdateContent?.text = mLatestVersionData?.releaseNotes

        var isForceUpdate = mLatestVersionData?.isForceUpdate ?: false
        if (isForceUpdate) {
            mIvClose?.visibility = View.GONE
        } else {
            mIvClose?.visibility = View.VISIBLE
        }

        downloadTask = lifecycleScope?.download(mLatestVersionData?.appDownloadUrl!!,context.filesDir.path)

        // listen download progress
        downloadTask?.progress()
            ?.onEach {
                withContext(Dispatchers.Main) {
                    Log.e(TAG,"percent = " + it.percent())
                    mPbDownloadProgress?.progress = it.percent().toInt()
                    mTvStatus?.text = it.percentStr()
                }
            }
            ?.launchIn(lifecycleScope!!)

        // or listen download state
        downloadTask?.state()
            ?.onEach {
                Log.e(TAG,"state = None..................it = $it")
                when (it) {
                    is State.None -> {
                        Log.e(TAG,"state = None..................")
                    }
                    is State.Waiting -> {
                        Log.e(TAG,"state = Waiting..................")
                    }
                    is State.Downloading -> {
                        Log.e(TAG,"state = Downloading..................")
                    }
                    is State.Failed -> {
                        Log.e(TAG,"state = Failed..................")
                    }
                    is State.Stopped -> {
                        Log.e(TAG,"state = Stopped..................")
                    }
                    is State.Succeed -> {
                        Log.e(TAG,"state = Succeed..................")
                        install()
                    }
                }
            }
            ?.launchIn(lifecycleScope!!)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvClose -> {
                dismiss()
            }
            R.id.mTvUpdateNow -> {
                download()
            }
        }
    }

    fun install () {
        InstallApkUtil.install(context,downloadTask?.file()!!)
    }

    fun download () {
        mTvUpdateNow?.visibility = View.INVISIBLE
        mPbDownloadProgress?.visibility = View.VISIBLE
        mTvStatus?.text = "正在更新..."
        downloadTask?.start()
    }

}