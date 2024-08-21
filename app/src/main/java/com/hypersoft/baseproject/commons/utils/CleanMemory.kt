package com.hypersoft.baseproject.commons.utils

import com.hypersoft.baseproject.commons.firebase.RemoteConstants

object CleanMemory {
    var isActivityRecreated = false
    fun clean() {
        RemoteConstants.reset()
    }

}