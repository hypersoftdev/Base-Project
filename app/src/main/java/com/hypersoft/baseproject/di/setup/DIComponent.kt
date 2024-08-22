package com.hypersoft.baseproject.di.setup

import com.hypersoft.baseproject.di.domain.manager.InternetManager
import com.hypersoft.baseproject.di.domain.observers.GeneralObserver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DIComponent : KoinComponent {

    // Managers
    val internetManager by inject<InternetManager>()

    // Observers
    val generalObserver by inject<GeneralObserver>()

}