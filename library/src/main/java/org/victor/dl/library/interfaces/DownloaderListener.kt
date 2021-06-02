package org.victor.dl.library.interfaces

import kotlinx.coroutines.channels.SendChannel
import okhttp3.ResponseBody
import org.victor.dl.library.data.DownloadConfig
import org.victor.dl.library.data.DownloadParam
import org.victor.dl.library.data.Progress
import org.victor.dl.library.data.QueryProgress
import retrofit2.Response


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownloaderListener
 * Author: Victor
 * Date: 2021/6/2 16:55
 * Description: 
 * -----------------------------------------------------------------
 */
interface DownloaderListener {
    var actor: SendChannel<QueryProgress>

    suspend fun queryProgress(): Progress

    suspend fun download(
        downloadParam: DownloadParam,
        downloadConfig: DownloadConfig,
        response: Response<ResponseBody>
    )
}