package org.victor.dl

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import org.victor.dl.library.data.State

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownLoadButton
 * Author: Victor
 * Date: 2024/02/03 11:11
 * Description: 
 * -----------------------------------------------------------------
 */

class DownLoadButton : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initialize()
    }

    fun initialize() {
        inflate(context,R.layout.layout_progress_button, this)
    }

    fun setState(state: State) {
        val mPbProgress = findViewById<ProgressBar>(R.id.progress)
        val mTvDownload = findViewById<TextView>(R.id.button)

        mPbProgress.max = state.progress.totalSize.toInt()
        mPbProgress.progress = state.progress.downloadSize.toInt()

        when (state) {
            is State.None -> {
                mTvDownload.text = "下载"
            }

            is State.Waiting -> {
                mTvDownload.text = "等待中"
            }

            is State.Downloading -> {
                mTvDownload.text = state.progress.percentStr()
            }

            is State.Failed -> {
                mTvDownload.text = "重试"
            }

            is State.Stopped -> {
                mTvDownload.text = "继续"
            }

            is State.Succeed -> {
                mTvDownload.text = "安装"
            }
        }
    }
}