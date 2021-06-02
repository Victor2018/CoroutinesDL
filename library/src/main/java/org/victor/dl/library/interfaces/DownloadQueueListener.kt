package org.victor.dl.library.interfaces

import org.victor.dl.library.core.DownloadTask


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownloadQueueListener
 * Author: Victor
 * Date: 2021/6/2 16:55
 * Description: 
 * -----------------------------------------------------------------
 */
interface DownloadQueueListener {
    suspend fun enqueue(task: DownloadTask)

    suspend fun dequeue(task: DownloadTask)
}