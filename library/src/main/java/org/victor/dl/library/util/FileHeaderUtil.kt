package org.victor.dl.library.util

import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString.Companion.decodeHex


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FileHeaderUtil
 * Author: Victor
 * Date: 2021/6/2 16:57
 * Description: 
 * -----------------------------------------------------------------
 */
class FileHeaderUtil(var totalSize: Long = 0L,
                     var totalRanges: Long = 0L) {
    companion object {
        const val FILE_HEADER_MAGIC_NUMBER = "a1b2c3d4e5f6"

        //How to calc: ByteString.decodeHex(FILE_HEADER_MAGIC_NUMBER).size() = 6
        const val FILE_HEADER_MAGIC_NUMBER_SIZE = 6L

        //total header size
        const val FILE_HEADER_SIZE = FILE_HEADER_MAGIC_NUMBER_SIZE + 16L
    }

    fun write(sink: BufferedSink, totalSize: Long, totalRanges: Long) {
        this.totalSize = totalSize
        this.totalRanges = totalRanges

        sink.apply {
            write(FILE_HEADER_MAGIC_NUMBER.decodeHex())
            writeLong(totalSize)
            writeLong(totalRanges)
        }
    }

    fun read(source: BufferedSource) {
        val header = source.readByteString(FILE_HEADER_MAGIC_NUMBER_SIZE).hex()
        if (header != FILE_HEADER_MAGIC_NUMBER) {
            throw IllegalStateException("not a tmp file")
        }
        totalSize = source.readLong()
        totalRanges = source.readLong()
    }

    fun check(totalSize: Long, totalRanges: Long): Boolean {
        return this.totalSize == totalSize &&
                this.totalRanges == totalRanges
    }
}