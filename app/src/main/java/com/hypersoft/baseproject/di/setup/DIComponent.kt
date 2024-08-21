package com.hypersoft.baseproject.di.setup

import com.hypersoft.baseproject.di.domain.observers.GeneralObserver
import com.hypersoft.baseproject.utilities.managers.InternetManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DIComponent : KoinComponent {

    // Managers
    val internetManager by inject<InternetManager>()

    // Observers
    val generalObserver by inject<GeneralObserver>()

}