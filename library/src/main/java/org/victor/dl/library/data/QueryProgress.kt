package org.victor.dl.library.data

import kotlinx.coroutines.CompletableDeferred


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: QueryProgress
 * Author: Victor
 * Date: 2021/6/2 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class QueryProgress(val completableDeferred: CompletableDeferred<Progress>)