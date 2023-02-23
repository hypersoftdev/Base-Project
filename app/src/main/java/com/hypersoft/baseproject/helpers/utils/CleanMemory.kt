package com.hypersoft.baseproject.helpers.utils

import com.hypersoft.baseproject.helpers.firebase.RemoteConstants

object CleanMemory {

    fun clean() {
        RemoteConstants.reset()
    }

}