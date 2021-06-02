package org.victor.dl.library.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.victor.dl.library.data.DownloadConfig
import org.victor.dl.library.interfaces.DownloadQueueListener
import java.util.concurrent.ConcurrentHashMap


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DefaultDownloadQueue
 * Author: Victor
 * Date: 2021/6/2 17:02
 * Description: 
 * -----------------------------------------------------------------
 */
class DefaultDownloadQueue private constructor(private val maxTask: Int) : DownloadQueueListener {
    companion object {
        private val lock = Any()
        private var instance: DefaultDownloadQueue? = null

        fun get(maxTask: Int = DownloadConfig.MAX_TASK_NUMBER): DefaultDownloadQueue {
            if (instance == null) {
                synchronized(lock) {
                    if (instance == null) {
                        instance = DefaultDownloadQueue(maxTask)
                    }
                }
            }
            return instance!!
        }
    }

    private val channel = Channel<DownloadTask>()
    private val tempMap = ConcurrentHashMap<String, DownloadTask>()

    init {
        GlobalScope.launch {
            repeat(maxTask) {
                launch {
                    channel.consumeEach {
                        if (contain(it)) {
                            it.suspendStart()
                            dequeue(it)
                        }
                    }
                }
            }
        }
    }

    override suspend fun enqueue(task: DownloadTask) {
        tempMap[task.param.tag()] = task
        channel.send(task)
    }

    override suspend fun dequeue(task: DownloadTask) {
        tempMap.remove(task.param.tag())
    }

    private fun contain(task: DownloadTask): Boolean {
        return tempMap[task.param.tag()] != null
    }
}