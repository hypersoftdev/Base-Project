package com.hypersoft.baseproject.helpers.koin

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.hypersoft.baseproject.helpers.firebase.RemoteConfiguration
import com.hypersoft.baseproject.helpers.managers.InternetManager
import com.hypersoft.baseproject.helpers.preferences.SharedPreferenceUtils
import com.hypersoft.baseproject.roomdb.db.GeneralProjectDatabase
import com.hypersoft.baseproject.roomdb.repository.GeneralProjectRepository
import com.hypersoft.baseproject.roomdb.viewmodel.GeneralProjectViewModel
import com.hypersoft.baseproject.ui.fragments.country.CountryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val applicationScope = CoroutineScope(SupervisorJob())

private val viewModelsModules = module {
    single { CountryViewModel() }
}

private val managerModules = module {
    single { InternetManager(androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) }
}

private val utilsModules = module {
    single { SharedPreferenceUtils(androidContext().getSharedPreferences("app_preferences", Application.MODE_PRIVATE)) }
}

private val dbModule = module {
    single { GeneralProjectDatabase.getDatabase(androidContext(), applicationScope).generalProjectDao() }

    single { GeneralProjectRepository(get()) }

    single { GeneralProjectViewModel(get()) }
}

private val firebaseModule = module {
    single { RemoteConfiguration(get()) }
}

val modulesList = listOf(viewModelsModules, utilsModules, managerModules, dbModule, firebaseModule)