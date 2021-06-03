package org.victor.dl

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.victor.dl.library.CoroutinesDL.download
import org.victor.dl.library.core.DownloadTask
import org.victor.dl.library.data.State
import org.victor.dl.library.util.InstallApkUtil


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

        downloadTask = GlobalScope.download(mLatestVersionData?.appDownloadUrl!!,context.filesDir.path)

        // listen download progress
        downloadTask?.progress()
            ?.onEach {
                Log.e(TAG,"percent = " + it.percent())
                mPbDownloadProgress?.setProgress((it.percent()).toInt())
            }
            ?.launchIn(GlobalScope)

        // or listen download state
        downloadTask?.state()
            ?.onEach {
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
                        InstallApkUtil.install(context,downloadTask?.file()!!)
                    }
                }
            }
            ?.launchIn(GlobalScope)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvClose -> {
                dismiss()
            }
            R.id.mTvUpdateNow -> {
                var isSucceed = downloadTask?.isSucceed() ?: false
                if (isSucceed) {
                    install()
                } else {
                    download()
                }
            }
        }
    }

    fun install () {
        mTvUpdateNow?.text = "安装"
        mTvUpdateNow?.visibility = View.VISIBLE
        mPbDownloadProgress?.visibility = View.GONE
        mTvStatus?.text = ""
    }

    fun download () {
        mTvUpdateNow?.visibility = View.INVISIBLE
        mPbDownloadProgress?.visibility = View.VISIBLE
        mTvStatus?.text = "正在更新..."
        downloadTask?.start()
    }
}