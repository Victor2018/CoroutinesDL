package org.victor.dl

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.victor.dl.library.CoroutinesDL.download
import org.victor.dl.library.core.DownloadTask
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
    var mTvDownload: DownLoadButton? = null
    var mTvNewVersion: MaterialTextView? = null
    var mTvUpdateContent: MaterialTextView? = null

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
        mTvDownload = findViewById(R.id.mTvDownload)
        mTvNewVersion = findViewById(R.id.mTvNewVersion)
        mTvUpdateContent = findViewById(R.id.mTvUpdateContent)

        mIvClose?.setOnClickListener(this)
        mTvDownload?.setOnClickListener(this)
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

        downloadTask = GlobalScope.download(
            mLatestVersionData?.appDownloadUrl ?: "",context.filesDir.path)

        downloadTask?.state()
            ?.onEach { mTvDownload?.setState(it) }
            ?.launchIn(lifecycleScope!!)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvClose -> {
                dismiss()
            }
            R.id.mTvDownload -> {
                download()
            }
        }
    }

    fun install () {
        InstallApkUtil.install(context,downloadTask?.file()!!)
    }

    fun download () {
        when {
            downloadTask!!.isSucceed() -> {
                install()
            }
            downloadTask!!.isStarted() -> {
                downloadTask!!.stop()
            }
            else -> {
                downloadTask!!.start()
            }
        }
    }

}