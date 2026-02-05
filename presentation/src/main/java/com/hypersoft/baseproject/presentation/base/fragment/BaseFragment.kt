package com.hypersoft.baseproject.presentation.base.fragment

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.hypersoft.baseproject.core.base.fragment.ParentFragment
import com.hypersoft.baseproject.core.di.DIComponent
import org.koin.android.ext.android.inject
import kotlin.getValue

abstract class BaseFragment<T : ViewBinding>(bindingFactory: (LayoutInflater) -> T) : ParentFragment<T>(bindingFactory) {

    protected val diComponent by lazy { DIComponent() }

    /**
     * BillingManager instance is used to invoke PlayBilling BottomSheet
     * */
    /*protected val billingManager by inject<BillingManager>()
    protected val remoteConfiguration by inject<RemoteConfiguration>()

    protected val appOpenAdManager by inject<AppOpenAdManager>()
    protected val interstitialAdsConfig by inject<InterstitialAdsConfig>()
    protected val bannerViewModel by inject<ViewModelBanner>()
    protected val nativeViewModel by inject<ViewModelNative>()*/
}