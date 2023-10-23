package com.hypersoft.baseproject.commons.firebase

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.commons.firebase.FirebaseUtils.recordException
import com.hypersoft.baseproject.commons.firebase.RemoteConstants.INTER_SPLASH_KEY
import com.hypersoft.baseproject.commons.firebase.RemoteConstants.NATIVE_SPLASH_KEY
import com.hypersoft.baseproject.commons.managers.InternetManager

class RemoteConfiguration(private val internetManager: InternetManager,
                          private val sharedPreferences: SharedPreferences) {

    private val configTag = "TAG_REMOTE_CONFIG"

    fun checkRemoteConfig(callback: (fetchSuccessfully: Boolean) -> Unit) {
        if (internetManager.isInternetConnected) {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 2 }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            fetchRemoteValues(callback)
        } else {
            Log.d(configTag, "checkRemoteConfig: Internet Not Found!")
            callback.invoke(false)
        }
    }

    private fun fetchRemoteValues(callback: (fetchSuccessfully: Boolean) -> Unit) {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                try {
                    updateRemoteValues(callback)
                } catch (ex: Exception) {
                    ex.recordException("RemoteConfiguration > fetchRemoteValues")
                    Log.d(configTag, "fetchRemoteValues: ${it.exception}")
                    callback.invoke(false)
                }
            } else {
                Log.d(configTag, "fetchRemoteValues: ${it.exception}")
                callback.invoke(false)
            }
        }.addOnFailureListener {
            Log.d(configTag, "fetchRemoteValues: ${it.message}")
            callback.invoke(false)
        }
    }

    @Throws(Exception::class)
    private fun updateRemoteValues(callback: (fetchSuccessfully: Boolean) -> Unit) {
        val remoteConfig = Firebase.remoteConfig

        setPrefRemoteValues(remoteConfig)
        getPrefRemoteValues()
        Log.d(configTag, "checkRemoteConfig: Fetched Successfully")
        callback.invoke(true)
    }

    fun getPrefRemoteValues() {
        RemoteConstants.rcvInterSplash = sharedPreferences.getInt(INTER_SPLASH_KEY, 0)
        RemoteConstants.rcvNativeSplash = sharedPreferences.getInt(NATIVE_SPLASH_KEY, 0)
    }

    @Throws(Exception::class)
    private fun setPrefRemoteValues(remoteConfig: FirebaseRemoteConfig) {
        sharedPreferences.edit().apply {
            putInt(INTER_SPLASH_KEY, remoteConfig[INTER_SPLASH_KEY].asLong().toInt())
            apply()
        }

        sharedPreferences.edit().apply {
            putInt(NATIVE_SPLASH_KEY, remoteConfig[NATIVE_SPLASH_KEY].asLong().toInt())
            apply()
        }
    }
}