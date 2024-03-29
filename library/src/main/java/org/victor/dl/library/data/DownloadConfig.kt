package org.victor.dl.library.data

import android.content.Context
import okhttp3.ResponseBody
import org.victor.dl.library.core.*
import org.victor.dl.library.interfaces.DownloadDispatcher
import org.victor.dl.library.interfaces.DownloadQueueListener
import org.victor.dl.library.interfaces.FileValidator
import org.victor.dl.library.interfaces.TaskManager
import retrofit2.Response


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownloadConfig
 * Author: Victor
 * Date: 2021/6/2 16:52
 * Description: 
 * -----------------------------------------------------------------
 */
class DownloadConfig(
    /**
     * 禁用断点续传
     */
    val disableRangeDownload: Boolean = false,

    /**
     * 下载管理
     */
    val taskManager: TaskManager = DefaultTaskManager,
    /**
     * 下载队列
     */
    val queue: DownloadQueueListener = DefaultDownloadQueue.get(),

    /**
     * 自定义header
     */
    val customHeader: Map<String, String> = emptyMap(),

    /**
     * 分片下载每片的大小
     */
    val rangeSize: Long = DEFAULT_RANGE_SIZE,
    /**
     * 分片下载并行数量
     */
    val rangeCurrency: Int = DEFAULT_RANGE_CURRENCY,

    /**
     * 下载器分发
     */
    val dispatcher: DownloadDispatcher = DefaultDownloadDispatcher,

    /**
     * 文件校验
     */
    val validator: FileValidator = DefaultFileValidator

    ) {
    companion object {
        /**
         * 默认的保存路径
         */
//        val DEFAULT_SAVE_PATH = ClarityPotion.context.filesDir.path
        val DEFAULT_SAVE_PATH = "/sdcard/"

        /**
         * 默认的分片大小
         */
        const val DEFAULT_RANGE_SIZE = 5L * 1024 * 1024

        /**
         * 单个任务同时下载的分片数量
         */
        const val DEFAULT_RANGE_CURRENCY = 5

        /**
         * 同时下载的任务数量
         */
        const val MAX_TASK_NUMBER = 3

        /**
         * 默认的Header
         */
        val RANGE_CHECK_HEADER = mapOf("Range" to "bytes=0-")

    }

    private val api = ApiClient.getApiService()

    suspend fun request(url: String, header: Map<String, String>): Response<ResponseBody> {
        val tempHeader = mutableMapOf<String, String>().also {
            it.putAll(customHeader)
            it.putAll(header)
        }
        return api.get(url, tempHeader)
    }

}