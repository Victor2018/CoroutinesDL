package org.victor.dl.library.interfaces

import okhttp3.ResponseBody
import org.victor.dl.library.data.DownloadParam
import retrofit2.Response
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FileValidator
 * Author: Victor
 * Date: 2021/6/2 16:55
 * Description: 
 * -----------------------------------------------------------------
 */
interface FileValidator {
    fun validate(file: File, param: DownloadParam, resp: Response<ResponseBody>): Boolean
}