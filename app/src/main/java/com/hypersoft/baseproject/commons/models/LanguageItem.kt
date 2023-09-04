package com.hypersoft.baseproject.commons.models

import com.google.errorprone.annotations.Keep

@Keep
data class LanguageItem(
    var languageCode: String,
    var languageName: String,
    var countryFlag: Int,
    var isSelected: Boolean
)
