package org.victor.dl.library.util

import okio.BufferedSink
import okio.BufferedSource


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FileContentUtil
 * Author: Victor
 * Date: 2021/6/2 16:57
 * Description: 
 * -----------------------------------------------------------------
 */
class FileContentUtil {
    val ranges = mutableListOf<RangeUtil>()

    fun write(
        sink: BufferedSink,
        totalSize: Long,
        totalRanges: Long,
        rangeSize: Long
    ) {
        ranges.clear()

        slice(totalSize, totalRanges, rangeSize)

        ranges.forEach {
            it.write(sink)
        }
    }

    fun read(source: BufferedSource, totalRanges: Long) {
        ranges.clear()
        for (i in 0 until totalRanges) {
            ranges.add(RangeUtil().read(source))
        }
    }

    fun downloadSize(): Long {
        var downloadSize = 0L
        ranges.forEach {
            downloadSize += it.completeSize()
        }
        return downloadSize
    }

    private fun slice(totalSize: Long, totalRanges: Long, rangeSize: Long) {
        var start = 0L

        for (i in 0 until totalRanges) {
            val end = if (i == totalRanges - 1) {
                totalSize - 1
            } else {
                start + rangeSize - 1
            }

            ranges.add(
                RangeUtil(
                    i,
                    start,
                    start,
                    end
                )
            )

            start += rangeSize
        }
    }
}