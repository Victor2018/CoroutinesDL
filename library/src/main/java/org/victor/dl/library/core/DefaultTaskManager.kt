package org.victor.dl.library.core

import org.victor.dl.library.interfaces.TaskManager
import java.util.concurrent.ConcurrentHashMap


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DefaultTaskManager
 * Author: Victor
 * Date: 2021/6/2 17:03
 * Description: 
 * -----------------------------------------------------------------
 */
object DefaultTaskManager : TaskManager {
    private val taskMap = ConcurrentHashMap<String, DownloadTask>()

    override fun add(task: DownloadTask): DownloadTask {
        if (taskMap[task.param.tag()] == null) {
            taskMap[task.param.tag()] = task
        }
        return taskMap[task.param.tag()]!!
    }

    override fun remove(task: DownloadTask) {
        taskMap.remove(task.param.tag())
    }
}