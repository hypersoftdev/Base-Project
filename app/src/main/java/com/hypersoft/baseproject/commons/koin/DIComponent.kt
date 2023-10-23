package com.hypersoft.baseproject.commons.koin

import com.hypersoft.baseproject.commons.firebase.RemoteConfiguration
import com.hypersoft.baseproject.commons.managers.InternetManager
import com.hypersoft.baseproject.commons.preferences.SharedPreferenceUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DIComponent : KoinComponent {

    // Utils
    val sharedPreferenceUtils by inject<SharedPreferenceUtils>()

    // Managers
    val internetManager by inject<InternetManager>()

    // Remote Configuration
    val remoteConfiguration by inject<RemoteConfiguration>()

}