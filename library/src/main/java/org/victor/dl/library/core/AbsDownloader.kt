package org.victor.dl.library.core

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import org.victor.dl.library.data.DownloadParam
import org.victor.dl.library.data.Progress
import org.victor.dl.library.data.QueryProgress
import org.victor.dl.library.interfaces.DownloaderListener
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AbsDownloader
 * Author: Victor
 * Date: 2021/6/2 17:01
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class AbsDownloader(protected val coroutineScope: CoroutineScope) : DownloaderListener {
    protected var totalSize: Long = 0L
    protected var downloadSize: Long = 0L
    protected var isChunked: Boolean = false

    private val progress = Progress()

    override var actor = GlobalScope.actor<QueryProgress>(Dispatchers.IO) {
        for (each in channel) {
            each.completableDeferred.complete(progress.also {
                it.downloadSize = downloadSize
                it.totalSize = totalSize
                it.isChunked = isChunked
            })
        }
    }

    override suspend fun queryProgress(): Progress {
        val ack = CompletableDeferred<Progress>()
        val queryProgress = QueryProgress(ack)
        actor.send(queryProgress)
        return ack.await()
    }

    fun DownloadParam.dir(): File {
        return File(savePath)
    }

    fun DownloadParam.file(): File {
        return File(savePath, saveName)
    }
}