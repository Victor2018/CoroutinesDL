package org.victor.dl.library

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import org.victor.dl.library.core.DownloadTask
import org.victor.dl.library.data.DownloadConfig
import org.victor.dl.library.data.DownloadParam


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CoroutinesDL
 * Author: Victor
 * Date: 2021/6/2 16:16
 * Description: 
 * -----------------------------------------------------------------
 */
object CoroutinesDL {
    fun CoroutineScope.download(
        url: String,
        savePath: String,
        saveName: String? = null
    ): DownloadTask {
        Log.e(javaClass.simpleName,"download-savePath = $savePath")
        val downloadParam = DownloadParam(url, saveName ?: "", savePath)
        val  downloadConfig = DownloadConfig()
        val task = DownloadTask(this, downloadParam, downloadConfig)
        return downloadConfig.taskManager.add(task)
    }

    fun CoroutineScope.download(
        downloadParam: DownloadParam
    ): DownloadTask {
        val  downloadConfig = DownloadConfig()
        val task = DownloadTask(this, downloadParam, downloadConfig)
        return downloadConfig.taskManager.add(task)
    }
}