package org.victor.dl.library.util

import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RangeUtil
 * Author: Victor
 * Date: 2021/6/2 16:58
 * Description: 
 * -----------------------------------------------------------------
 */
class RangeUtil(
    var index: Long = 0L,
    var start: Long = 0L,
    var current: Long = 0L,
    var end: Long = 0L
) {

    companion object {
        const val RANGE_SIZE = 32L //each Long is 8 bytes
    }

    fun write(sink: BufferedSink): RangeUtil {
        sink.apply {
            writeLong(index)
            writeLong(start)
            writeLong(current)
            writeLong(end)
        }
        return this
    }

    fun read(source: BufferedSource): RangeUtil {
        val buffer = Buffer()
        source.readFully(buffer,
            RANGE_SIZE
        )

        buffer.apply {
            index = readLong()
            start = readLong()
            current = readLong()
            end = readLong()
        }

        return this
    }

    fun isComplete() = (current - end) == 1L

    fun remainSize() = end - current + 1

    fun completeSize() = current - start

    /**
     * Return the starting position of the range
     */
    fun startByte() = FileHeaderUtil.FILE_HEADER_SIZE + RANGE_SIZE * index
}