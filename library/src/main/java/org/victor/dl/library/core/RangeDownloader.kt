package org.victor.dl.library.core

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import okhttp3.ResponseBody
import org.victor.dl.library.data.DownloadConfig
import org.victor.dl.library.data.DownloadParam
import org.victor.dl.library.util.*
import org.victor.dl.library.util.RangeUtil.Companion.RANGE_SIZE
import retrofit2.Response
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RangeDownloader
 * Author: Victor
 * Date: 2021/6/2 17:04
 * Description: 
 * -----------------------------------------------------------------
 */
@OptIn(ObsoleteCoroutinesApi::class)
class RangeDownloader(coroutineScope: CoroutineScope) : AbsDownloader(coroutineScope) {
    val TAG = "RangeDownloader"
    private lateinit var file: File
    private lateinit var shadowFile: File
    private lateinit var tmpFile: File
    private lateinit var rangeTmpFile: RangeTmpFileUtil

    override suspend fun download(
        downloadParam: DownloadParam,
        downloadConfig: DownloadConfig,
        response: Response<ResponseBody>
    ) {
        Log.e(TAG,"download()......")
        try {
            file = downloadParam.file()
            shadowFile = file.shadow()
            tmpFile = file.tmp()

            val alreadyDownloaded = checkFiles(downloadParam, downloadConfig, response)
            Log.e(TAG,"download()......alreadyDownloaded = $alreadyDownloaded")

            if (alreadyDownloaded) {
                downloadSize = response.contentLength()
                totalSize = response.contentLength()
            } else {
                val last = rangeTmpFile.lastProgress()
                downloadSize = last.downloadSize
                totalSize = last.totalSize
                startDownload(downloadParam, downloadConfig)
            }
        } finally {
            response.closeQuietly()
        }
    }

    private fun checkFiles(
        param: DownloadParam,
        config: DownloadConfig,
        response: Response<ResponseBody>
    ): Boolean {
        Log.e(TAG,"checkFiles()......")
        var alreadyDownloaded = false

        //make sure dir is exists
        val fileDir = param.dir()
        if (!fileDir.exists() || !fileDir.isDirectory) {
            fileDir.mkdirs()
        }

        val contentLength = response.contentLength()
        val rangeSize = config.rangeSize
        val totalRanges = response.calcRanges(rangeSize)

        if (file.exists()) {
            if (config.validator.validate(file, param, response)) {
                alreadyDownloaded = true
            } else {
                file.delete()
                recreateFiles(contentLength, totalRanges, rangeSize)
            }
        } else {
            if (shadowFile.exists() && tmpFile.exists()) {
                rangeTmpFile = RangeTmpFileUtil(tmpFile)
                rangeTmpFile.read()

                if (!rangeTmpFile.isValid(contentLength, totalRanges)) {
                    recreateFiles(contentLength, totalRanges, rangeSize)
                }
            } else {
                recreateFiles(contentLength, totalRanges, rangeSize)
            }
        }

        return alreadyDownloaded
    }

    private fun recreateFiles(contentLength: Long, totalRanges: Long, rangeSize: Long) {
        Log.e(TAG,"recreateFiles()......")
        tmpFile.recreate()
        shadowFile.recreate(contentLength)
        rangeTmpFile = RangeTmpFileUtil(tmpFile)
        rangeTmpFile.write(contentLength, totalRanges, rangeSize)
    }

    private suspend fun startDownload(param: DownloadParam, config: DownloadConfig) {
        Log.e(TAG,"startDownload()......")
        val progressChannel = coroutineScope.actor<Int> {
            channel.consumeEach { downloadSize += it }
        }

        rangeTmpFile.undoneRanges().parallel(max = config.rangeCurrency) {
            it.download(param, config, progressChannel)
        }

        progressChannel.close()

        shadowFile.renameTo(file)
        tmpFile.delete()
    }

    private suspend fun RangeUtil.download(
        param: DownloadParam,
        config: DownloadConfig,
        sendChannel: SendChannel<Int>
    ) = coroutineScope {
        Log.e(TAG,"RangeUtil.download()......")
        val deferred = async(Dispatchers.IO) {
            val url = param.url
            val rangeHeader = mapOf("Range" to "bytes=${current}-${end}")

            val response = config.request(url, rangeHeader)
            if (!response.isSuccessful || response.body() == null) {
                throw RuntimeException("Request failed!")
            }

            response.body()?.use {
                it.byteStream().use { source ->
                    val tmpFileBuffer = tmpFile.mappedByteBuffer(startByte(), RANGE_SIZE)
                    val shadowFileBuffer = shadowFile.mappedByteBuffer(current, remainSize())

                    val buffer = ByteArray(8192)
                    var readLen = source.read(buffer)

                    while (isActive && readLen != -1) {
                        shadowFileBuffer.put(buffer, 0, readLen)
                        current += readLen

                        tmpFileBuffer.putLong(16, current)

                        sendChannel.send(readLen)

                        readLen = source.read(buffer)
                    }
                }
            }
        }
        deferred.await()
    }
}