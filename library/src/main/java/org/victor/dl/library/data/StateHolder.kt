package org.victor.dl.library.data

import android.util.Log


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StateHolder
 * Author: Victor
 * Date: 2021/6/2 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class StateHolder {
    val TAG = "StateHolder"
    val none by lazy { State.None() }
    val waiting by lazy { State.Waiting() }
    val downloading by lazy { State.Downloading() }
    val stopped by lazy { State.Stopped() }
    val failed by lazy { State.Failed() }
    val succeed by lazy { State.Succeed() }

    var currentState: State = none

    fun isStarted(): Boolean {
        return currentState is State.Waiting || currentState is State.Downloading
    }

    fun isFailed(): Boolean {
        return currentState is State.Failed
    }

    fun isSucceed(): Boolean {
        return currentState is State.Succeed
    }

    fun canStart(): Boolean {
        return currentState is State.None || currentState is State.Failed || currentState is State.Stopped
    }

    fun isEnd(): Boolean {
        return currentState is State.None || currentState is State.Waiting || currentState is State.Stopped || currentState is State.Failed || currentState is State.Succeed
    }

    fun updateState(new: State, progress: Progress): State {
        currentState = new.apply { this.progress = progress }
        return currentState
    }
}