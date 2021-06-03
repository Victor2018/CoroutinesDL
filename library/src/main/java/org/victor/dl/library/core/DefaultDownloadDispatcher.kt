package org.victor.dl.library.core

import android.annotation.SuppressLint
import android.util.Log
import okhttp3.ResponseBody
import org.victor.dl.library.interfaces.DownloadDispatcher
import org.victor.dl.library.interfaces.DownloaderListener
import org.victor.dl.library.util.isSupportRange
import retrofit2.Response


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DefaultDownloadDispatcher
 * Author: Victor
 * Date: 2021/6/2 17:02
 * Description: 
 * -----------------------------------------------------------------
 */
object DefaultDownloadDispatcher : DownloadDispatcher {
    val TAG = "DefaultDownloadDispatcher"
    @SuppressLint("LongLogTag")
    override fun dispatch(downloadTask: DownloadTask, resp: Response<ResponseBody>): DownloaderListener {
        return if (resp.isSupportRange()) {
            Log.e(TAG,"dispatch-RangeDownloader")
            RangeDownloader(downloadTask.coroutineScope)
        } else {
            Log.e(TAG,"dispatch-NormalDownloader")
            NormalDownloader(downloadTask.coroutineScope)
        }
    }
}