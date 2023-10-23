package com.hypersoft.baseproject.commons.models

import androidx.annotation.DrawableRes
import com.google.errorprone.annotations.Keep

@Keep
data class LanguageItem(
    var languageCode: String,
    var languageName: String,
    @DrawableRes var countryFlag: Int,
    var selected: Boolean
)
