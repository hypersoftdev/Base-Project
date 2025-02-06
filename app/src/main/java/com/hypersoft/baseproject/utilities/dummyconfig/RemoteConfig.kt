package com.hypersoft.baseproject.utilities.dummyconfig

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RemoteConfig {

    fun checkRemoteConfig(callback: (fetchSuccessfully: Boolean) -> Unit) {

        CoroutineScope(IO).launch {
            delay(3000)
            callback.invoke(false)
            delay(3000)
            callback.invoke(false)
            delay(3000)
            callback.invoke(true)
        }
    }

}