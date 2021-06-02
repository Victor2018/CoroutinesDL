package org.victor.dl

import android.app.Application


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: App
 * Author: Victor
 * Date: 2021/6/2 17:38
 * Description: 
 * -----------------------------------------------------------------
 */
class App: Application() {
    companion object {
        private lateinit var instance : App
        fun get() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}