package com.flash.worker.lib.coremodel.http.converter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.parser.Feature
import com.alibaba.fastjson.serializer.SerializeConfig
import com.alibaba.fastjson.serializer.SerializerFeature
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Converter
import java.io.IOException
import kotlin.jvm.Throws


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FastJsonRequestBodyConverter
 * Author: Victor
 * Date: 2020/11/30 12:08
 * Description: 
 * -----------------------------------------------------------------
 */
class FastJsonRequestBodyConverter<T>(
    var config: SerializeConfig?,
    var features: Array<out SerializerFeature>?
) :
    Converter<T, RequestBody?> {

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val content: ByteArray
        content = if (config != null) {
            if (features != null) {
                JSON.toJSONBytes(value, config, *features!!)
            } else {
                JSON.toJSONBytes(value, config)
            }
        } else {
            if (features != null) {
                JSON.toJSONBytes(value, *features!!)
            } else {
                JSON.toJSONBytes(value)
            }
        }
        return RequestBody.create(MEDIA_TYPE, content)
    }

    companion object {
        private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
    }
}
