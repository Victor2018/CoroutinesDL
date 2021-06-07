package org.victor.dl.library.core

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.victor.dl.library.data.*
import org.victor.dl.library.interfaces.DownloaderListener
import org.victor.dl.library.util.clear
import org.victor.dl.library.util.closeQuietly
import org.victor.dl.library.util.fileName
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownloadTask
 * Author: Victor
 * Date: 2021/6/2 17:03
 * Description: 
 * -----------------------------------------------------------------
 */
@OptIn(ObsoleteCoroutinesApi::class, FlowPreview::class, ExperimentalCoroutinesApi::class)
open class DownloadTask(
    val coroutineScope: CoroutineScope,
    val param: DownloadParam,
    val config: DownloadConfig
) {
    val TAG = "DownloadTask"
    private val stateHolder by lazy { StateHolder() }

    private var downloadJob: Job? = null
    private var downloader: DownloaderListener? = null

    private val downloadProgressFlow = MutableStateFlow(0)
    private val downloadStateFlow = MutableStateFlow<State>(stateHolder.none)
    private var downloadComplete: Boolean = false

    fun isStarted(): Boolean {
        return stateHolder.isStarted()
    }

    fun isFailed(): Boolean {
        return stateHolder.isFailed()
    }

    fun isSucceed(): Boolean {
        return stateHolder.isSucceed()
    }

    fun canStart(): Boolean {
        return stateHolder.canStart()
    }

    private fun checkJob() = downloadJob?.isActive == true

    /**
     * 获取下载文件
     */
    fun file(): File? {
        return if (param.saveName.isNotEmpty() && param.savePath.isNotEmpty()) {
            File(param.savePath, param.saveName)
        } else {
            null
        }
    }

    /**
     * 开始下载，添加到下载队列
     */
    fun start() {
        Log.e(javaClass.simpleName,"start..........")
        downloadComplete = false
        coroutineScope.launch {
            if (checkJob()) return@launch

            notifyWaiting()
            try {
                config.queue.enqueue(this@DownloadTask)
            } catch (e: Exception) {
                e.printStackTrace()
                if (e !is CancellationException) {
                    notifyFailed()
                }
            }
        }
    }

    /**
     * 开始下载并等待下载完成，直接开始下载，不添加到下载队列
     */
    suspend fun suspendStart() {
        Log.e(javaClass.simpleName,"suspendStart..........")
        if (checkJob()) return

        downloadJob?.cancel()
        val errorHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            if (throwable !is CancellationException) {
                throwable.printStackTrace()
                coroutineScope.launch {
                    notifyFailed()
                }
            }
        }
        downloadJob = coroutineScope.launch(errorHandler + Dispatchers.IO) {
            Log.e(javaClass.simpleName,"suspendStart..........param.url = " + param.url)
            val response = config.request(param.url, DownloadConfig.RANGE_CHECK_HEADER)
            try {
                if (!response.isSuccessful || response.body() == null) {
                    throw RuntimeException("request failed")
                }

                if (param.saveName.isEmpty()) {
                    param.saveName = response.fileName()
                }
                if (param.savePath.isEmpty()) {
//                    param.savePath = DownloadConfig.DEFAULT_SAVE_PATH
                    Log.e(javaClass.simpleName,"suspendStart..........savePath is empty")
                }

                if (downloader == null) {
                    downloader = config.dispatcher.dispatch(this@DownloadTask, response)
                }

                Log.e(javaClass.simpleName,"suspendStart..........notifyStarted")
                notifyStarted()

                val deferred = async(Dispatchers.IO) { downloader?.download(param, config, response) }
                Log.e(javaClass.simpleName,"suspendStart..........notifySucceed")
                deferred.await()

                notifySucceed()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e !is CancellationException) {
                    notifyFailed()
                }
            } finally {
                response.closeQuietly()
            }
        }
        downloadJob?.join()
    }

    /**
     * 停止下载
     */
    fun stop() {
        coroutineScope.launch {
            if (isStarted()) {
                config.queue.dequeue(this@DownloadTask)
                downloadJob?.cancel()
                notifyStopped()
            }
        }
    }

    /**
     * 移除任务
     */
    fun remove(deleteFile: Boolean = true) {
        stop()
        config.taskManager.remove(this)
        if (deleteFile) {
            file()?.clear()
        }
    }

    /**
     * @param interval 更新进度间隔时间，单位ms
     * @param ensureLast 能否收到最后一个进度
     */
    fun progress(interval: Long = 200, ensureLast: Boolean = true): Flow<Progress> {
        return downloadProgressFlow.flatMapConcat {
            channelFlow {
                while (currentCoroutineContext().isActive) {
                    val progress = getProgress()

                    if (downloadComplete && stateHolder.isEnd()) {
                        if (!ensureLast) {
                            break
                        }
                    }

                    if (progress.percent() > 0 && !downloadComplete) {
                        send(progress)
                        Log.e(TAG,"url = ${param.url}")
                        Log.e(TAG,"progress =  ${progress.percentStr()}")
                        downloadComplete = progress.isComplete()
                    }

                    if (progress.isComplete()) break

                    delay(interval)
                }
            }
        }
    }

    /**
     * @param interval 更新进度间隔时间，单位ms
     */
    fun state(interval: Long = 200): Flow<State> {
        return downloadStateFlow.combine(progress(interval, ensureLast = false)) { l, r -> l.apply { progress = r } }
    }

    suspend fun getProgress(): Progress {
        return downloader?.queryProgress() ?: Progress()
    }

    fun getState() = stateHolder.currentState

    private suspend fun notifyWaiting() {
        stateHolder.updateState(stateHolder.waiting, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        Log.e(TAG,"notifyWaiting-url = ${param.url} download task waiting.")
    }

    private suspend fun notifyStarted() {
        stateHolder.updateState(stateHolder.downloading, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        downloadProgressFlow.value = downloadProgressFlow.value + 1
        Log.e(TAG,"notifyStarted-url = ${param.url} download task start.")
    }

    private suspend fun notifyStopped() {
        stateHolder.updateState(stateHolder.stopped, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        Log.e(TAG,"notifyStopped-url = ${param.url} download task stopped.")
    }

    private suspend fun notifyFailed() {
        stateHolder.updateState(stateHolder.failed, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        Log.e(TAG,"notifyFailed-url = ${param.url} download task failed.")
    }

    private suspend fun notifySucceed() {
        stateHolder.updateState(stateHolder.succeed, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        Log.e(TAG,"notifySucceed-url = ${param.url} download task succeed.")
    }

    private fun Progress.isComplete(): Boolean {
        return totalSize > 0 && totalSize == downloadSize
    }

}