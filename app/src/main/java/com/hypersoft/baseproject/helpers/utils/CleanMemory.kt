package com.hypersoft.baseproject.helpers.utils

import com.hypersoft.baseproject.helpers.firebase.RemoteConstants

object CleanMemory {
    var isActivityRecreated = false
    fun clean() {
        RemoteConstants.reset()
    }

}