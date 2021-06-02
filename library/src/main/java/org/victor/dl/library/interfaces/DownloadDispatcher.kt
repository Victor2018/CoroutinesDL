package org.victor.dl.library.interfaces

import okhttp3.ResponseBody
import org.victor.dl.library.core.DownloadTask
import retrofit2.Response


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownloadDispatcher
 * Author: Victor
 * Date: 2021/6/2 16:54
 * Description: 
 * -----------------------------------------------------------------
 */
interface DownloadDispatcher {
    fun dispatch(downloadTask: DownloadTask, resp: Response<ResponseBody>): DownloaderListener
}