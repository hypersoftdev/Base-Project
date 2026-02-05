package com.hypersoft.baseproject.core.firebase

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
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

    /**
     *  // Native Ad
     *      .forNativeAd { nativeAd ->
     *          // Attach revenue listener immediately
     *          nativeAd.setOnPaidEventListener { adValue ->
     *              val revenueInDollars = adValue.valueMicros.toFloat() / 1_000_000f
     *              revenueInDollars.logRevenueEvent(context)
     *          }
     *      }
     *
     *  // Interstitial Ad
     *      override fun onAdShowedFullScreenContent() {
     *          interstitialAd?.onPaidEventListener = OnPaidEventListener {adValue ->
     *              val revenueInDollars = adValue.valueMicros.toFloat() / 1_000_000f
     *              revenueInDollars.logRevenueEvent(context)
     *          }
     *      }
     *
     *  // Banner Ad
     *      override fun onAdLoaded() {
     *          super.onAdLoaded()
     *          adView.onPaidEventListener = OnPaidEventListener { adValue ->
     *              val revenueInDollars = adValue.valueMicros.toFloat() / 1_000_000f
     *              revenueInDollars.logRevenueEvent(context)
     *          }
     *      }
     *
     *  // AppOpen
     *      override fun onAdLoaded(ad: AppOpenAd) {
     *          ad.onPaidEventListener = OnPaidEventListener { adValue ->
     *              val revenueInDollars = adValue.valueMicros.toFloat() / 1_000_000f
     *              revenueInDollars.logRevenueEvent(context)
     *          }
     *      }
     */

    fun Float.logRevenueEvent(context: Context, threshold: Float = 0.1f) {
        val key = "TaichiTroasCache"
        val sharedPreferences = context.getSharedPreferences("rossPref", Context.MODE_PRIVATE)
        val newCache = sharedPreferences.getFloat(key, 0f) + this

        sharedPreferences.edit {
            if (newCache >= threshold) {
                try {
                    val bundle = Bundle().apply {
                        putDouble(FirebaseAnalytics.Param.VALUE, newCache.toDouble())
                        putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                    }
                    Firebase.analytics.logEvent("Total_Ads_Revenue_01", bundle)
                    Log.d(TAG_FIREBASE, "Firebase event logged: $bundle")
                } catch (ex: Exception) {
                    Log.e(TAG_FIREBASE, "Error posting Firebase event for revenue $this", ex)
                    ex.recordException("Posting Event > $this")
                }
                putFloat(key, 0f) // reset cache
            } else {
                putFloat(key, newCache)
            }
        }
    }
}