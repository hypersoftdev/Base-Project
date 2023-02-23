package com.hypersoft.baseproject.helpers.koin

import com.hypersoft.baseproject.helpers.firebase.RemoteConfiguration
import com.hypersoft.baseproject.helpers.managers.InternetManager
import com.hypersoft.baseproject.helpers.preferences.SharedPreferenceUtils
import com.hypersoft.baseproject.roomdb.viewmodel.GeneralProjectViewModel
import com.hypersoft.baseproject.ui.fragments.country.CountryViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DIComponent : KoinComponent {

    // Utils
    val sharedPreferenceUtils by inject<SharedPreferenceUtils>()

    // Managers
    val internetManager by inject<InternetManager>()

    // ViewModels
    val countryViewModel by inject<CountryViewModel>()
    val generalProjectViewModel by inject<GeneralProjectViewModel>()

    // Remote Configuration
    val remoteConfiguration by inject<RemoteConfiguration>()

}