package com.hypersoft.baseproject.di.setup

import com.hypersoft.baseproject.app.features.remoteConfig.presentation.viewmodel.NetworkViewModel
import com.hypersoft.baseproject.di.domain.manager.InternetManager
import com.hypersoft.baseproject.di.domain.observers.GeneralObserver
import com.hypersoft.baseproject.utilities.dummyconfig.RemoteConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DIComponent : KoinComponent {

    // Managers
    val internetManager by inject<InternetManager>()

    // Observers
    val generalObserver by inject<GeneralObserver>()
    val remoteConfiguration by inject<RemoteConfig>()

    // NetworkUseCase
    val networkUseCase by inject<NetworkUseCase>()
    val networkViewModel by inject<NetworkViewModel>()
}