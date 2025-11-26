package com.hypersoft.baseproject.core.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.installations.FirebaseInstallations
import com.hypersoft.baseproject.core.constants.Constants.TAG
import com.hypersoft.baseproject.core.constants.Constants.TAG_FIREBASE

object FirebaseUtils {

    fun Throwable.recordException(log: String) {
        try {
            FirebaseCrashlytics.getInstance().log(log)
            FirebaseCrashlytics.getInstance().recordException(this)
            Log.d(TAG, "recordException: ${this.message.toString()}")
        } catch (e: Exception) {
            Log.d(TAG, "recordException: catch: ${e.message.toString()}")
        }
    }

    fun String.postFirebaseEvent() {
        val firebaseAnalytics = Firebase.analytics
        try {
            val bundle = Bundle().also {
                it.putString(this, this)
            }
            firebaseAnalytics.logEvent(this, bundle)
            Log.d(TAG_FIREBASE, "postFirebaseEvent: Bundle: $bundle")
        } catch (ex: Exception) {
            ex.recordException("Posting Event > $this")
        }
    }

    fun getDeviceToken() {
        // Add this 'id' in firebase AB testing console as a testing device
        FirebaseInstallations.getInstance().getToken(false)
            .addOnCompleteListener { task ->
                when (task.isSuccessful && task.result != null) {
                    true -> Log.d(TAG_FIREBASE, "Installation auth token: " + task.result.token)
                    false -> Log.e(TAG_FIREBASE, "Unable to get Installation auth token")
                }
            }
    }
}