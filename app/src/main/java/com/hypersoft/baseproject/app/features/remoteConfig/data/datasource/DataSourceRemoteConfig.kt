package com.hypersoft.baseproject.app.features.remoteConfig.data.datasource

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by: Sohaib Ahmed
 * Date: 2/6/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class DataSourceRemoteConfig {

    fun checkRemoteConfig(responseCallback: (fetchSuccessfully: Boolean, errorMessage: String?) -> Unit) {
        CoroutineScope(IO).launch {
            delay(3000)
            responseCallback.invoke(false, "Failed Fetching")
            delay(3000)
            responseCallback.invoke(false, "Failed Fetching")
            delay(3000)
            responseCallback.invoke(true, null)
        }
    }
}