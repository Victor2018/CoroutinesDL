package org.victor.dl.library.data


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: State
 * Author: Victor
 * Date: 2021/6/2 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
sealed class State() {
    var progress: Progress =
        Progress()
        internal set

    class None : State()
    class Waiting : State()
    class Downloading : State()
    class Stopped : State()
    class Failed : State()
    class Succeed : State()
}