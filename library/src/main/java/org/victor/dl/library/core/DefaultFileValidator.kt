package org.victor.dl.library.core

import okhttp3.ResponseBody
import org.victor.dl.library.data.DownloadParam
import org.victor.dl.library.interfaces.FileValidator
import org.victor.dl.library.util.contentLength
import retrofit2.Response
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DefaultFileValidator
 * Author: Victor
 * Date: 2021/6/2 17:03
 * Description: 
 * -----------------------------------------------------------------
 */
object DefaultFileValidator: FileValidator {
    override fun validate(
        file: File,
        param: DownloadParam,
        resp: Response<ResponseBody>
    ): Boolean {
        return file.length() == resp.contentLength()
    }
}