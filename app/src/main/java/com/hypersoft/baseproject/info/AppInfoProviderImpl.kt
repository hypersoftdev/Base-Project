package com.hypersoft.baseproject.info

import com.hypersoft.baseproject.BuildConfig
import com.hypersoft.baseproject.core.info.AppInfoProvider

class AppInfoProviderImpl : AppInfoProvider {

    override val versionName: String get() = BuildConfig.VERSION_NAME

}