package org.victor.dl.library.data


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownloadParam
 * Author: Victor
 * Date: 2021/6/2 16:52
 * Description: 
 * -----------------------------------------------------------------
 */
open class DownloadParam(
    var url: String,
    var saveName: String = "",
    var savePath: String = "",
    var extra: String = ""
) {

    /**
     * Each task with unique tag.
     */
    open fun tag() = url


    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true

        return if (other is DownloadParam) {
            tag() == other.tag()
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return tag().hashCode()
    }
}