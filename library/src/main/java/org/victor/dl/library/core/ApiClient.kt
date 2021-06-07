package org.victor.dl.library.core

import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.victor.dl.library.interfaces.DownloadApiService
import retrofit2.Retrofit
import java.util.*
import java.util.concurrent.TimeUnit


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ApiClient
 * Author: Victor
 * Date: 2021/6/2 17:01
 * Description: 
 * -----------------------------------------------------------------
 */
object ApiClient {
    const val TIME_OUT:Long = 30
    const val READ_TIME_OUT:Long = 120
    const val WRITE_TIME_OUT:Long = 120
    const val FAKE_BASE_URL = "http://www.baidu.com"

    /**
     * 用于存储ApiService
     */
    private val map = mutableMapOf<Class<*>, Any>()

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(FAKE_BASE_URL)
            .client(okHttpClient)
//            .addConverterFactory(FastJsonConverterFactory.create())
            .build()
    }

    /**
     * 获取ApiService单例对象
     */
    private fun <T : Any> getService(apiClass: Class<T>): T{
        //重入锁单例避免多线程安全问题
        return if (map[apiClass] == null) {
            synchronized(ApiClient::class.java) {
                val t = retrofit.create(apiClass)
                if (map[apiClass] == null) {
                    map[apiClass] = t
                }
                t
            }
        } else {
            map[apiClass] as T
        }
    }

    fun getApiService(): DownloadApiService {
        return getService(DownloadApiService::class.java)
    }
}