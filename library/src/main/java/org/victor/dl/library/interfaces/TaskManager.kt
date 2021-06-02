package org.victor.dl.library.interfaces

import org.victor.dl.library.core.DownloadTask


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskManager
 * Author: Victor
 * Date: 2021/6/2 16:56
 * Description: 
 * -----------------------------------------------------------------
 */
interface TaskManager {
    fun add(task: DownloadTask): DownloadTask

    fun remove(task: DownloadTask)
}