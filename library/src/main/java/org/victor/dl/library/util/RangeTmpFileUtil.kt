package org.victor.dl.library.util

import okio.buffer
import okio.sink
import okio.source
import org.victor.dl.library.data.Progress
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RangeTmpFileUtil
 * Author: Victor
 * Date: 2021/6/2 16:56
 * Description: 
 * -----------------------------------------------------------------
 */
class RangeTmpFileUtil(private val tmpFile: File) {
    private val fileHeader = FileHeaderUtil()
    private val fileContent = FileContentUtil()

    fun write(totalSize: Long, totalRanges: Long, rangeSize: Long) {
        tmpFile.sink().buffer().use {
            fileHeader.write(it, totalSize, totalRanges)
            fileContent.write(it, totalSize, totalRanges, rangeSize)
        }
    }

    fun read() {
        tmpFile.source().buffer().use {
            fileHeader.read(it)
            fileContent.read(it, fileHeader.totalRanges)
        }
    }

    fun isValid(totalSize: Long, totalRanges: Long): Boolean {
        return fileHeader.check(totalSize, totalRanges)
    }

    fun undoneRanges(): List<RangeUtil> {
        return fileContent.ranges.filter { !it.isComplete() }
    }

    fun lastProgress(): Progress {
        val totalSize = fileHeader.totalSize
        val downloadSize = fileContent.downloadSize()

        return Progress(downloadSize, totalSize)
    }
}
