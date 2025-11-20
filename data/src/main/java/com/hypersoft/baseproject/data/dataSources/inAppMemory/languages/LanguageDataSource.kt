package com.hypersoft.baseproject.data.dataSources.inAppMemory.languages

import com.hypersoft.baseproject.core.R
import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.entities.Language

class LanguageDataSource {

    fun getLanguages(): List<Language> {
        return listOf(
            Language(languageCode = "en", languageName = "English", flagIcon = R.drawable.flag_en, isSelected = false),
            Language(languageCode = "af", languageName = "Afrikaans", flagIcon = R.drawable.flag_af, isSelected = false),
            Language(languageCode = "ar", languageName = "Arabic (عربي)", flagIcon = R.drawable.flag_ar, isSelected = false),
            Language(languageCode = "az", languageName = "Azerbaijani (azərbaycan dili)", flagIcon = R.drawable.flag_az, isSelected = false),
            Language(languageCode = "be", languageName = "Belarusian (беларуская мова)", flagIcon = R.drawable.flag_be, isSelected = false),
            Language(languageCode = "bg", languageName = "Bulgarian (български език)", flagIcon = R.drawable.flag_bg, isSelected = false),
            Language(languageCode = "bs", languageName = "Bosnian (bosanski jezik)", flagIcon = R.drawable.flag_bs, isSelected = false),
            Language(languageCode = "ca", languageName = "Catalan (català)", flagIcon = R.drawable.flag_ca, isSelected = false),
            Language(languageCode = "cs", languageName = "Czech (čeština)", flagIcon = R.drawable.flag_cs, isSelected = false),
            Language(languageCode = "da", languageName = "Danish (dansk)", flagIcon = R.drawable.flag_da, isSelected = false),
            Language(languageCode = "de", languageName = "German (Deutsch)", flagIcon = R.drawable.flag_de, isSelected = false),
            Language(languageCode = "el", languageName = "Greek (ελληνικά)", flagIcon = R.drawable.flag_el, isSelected = false),
            Language(languageCode = "es", languageName = "Spanish (Española)", flagIcon = R.drawable.flag_es, isSelected = false),
            Language(languageCode = "et", languageName = "Estonian (eesti keel)", flagIcon = R.drawable.flag_et, isSelected = false),
            Language(languageCode = "eu", languageName = "Basque (euskara)", flagIcon = R.drawable.flag_eu, isSelected = false),
            Language(languageCode = "fa", languageName = "Persian (فارسي)", flagIcon = R.drawable.flag_fa, isSelected = false),
            Language(languageCode = "fi", languageName = "Finnish (Suomalainen)", flagIcon = R.drawable.flag_fi, isSelected = false),
            Language(languageCode = "fr", languageName = "French (Français)", flagIcon = R.drawable.flag_fr, isSelected = false),
            Language(languageCode = "gu", languageName = "Gujarati (ગુજરાતી)", flagIcon = R.drawable.flag_gu, isSelected = false),
            Language(languageCode = "he", languageName = "Hebrew (עִבְרִית)", flagIcon = R.drawable.flag_he, isSelected = false),
            Language(languageCode = "hi", languageName = "Hindi (हिन्दी)", flagIcon = R.drawable.flag_hi, isSelected = false),
            Language(languageCode = "hr", languageName = "Croatian (hrvatski jezik)", flagIcon = R.drawable.flag_hr, isSelected = false),
            Language(languageCode = "hu", languageName = "Hungarian (magyar)", flagIcon = R.drawable.flag_hu, isSelected = false),
            Language(languageCode = "hy", languageName = "Armenian (հայերեն)", flagIcon = R.drawable.flag_hy, isSelected = false),
            Language(languageCode = "is", languageName = "Icelandic (íslenska)", flagIcon = R.drawable.flag_is, isSelected = false),
            Language(languageCode = "it", languageName = "Italian", flagIcon = R.drawable.flag_it, isSelected = false),
            Language(languageCode = "ja", languageName = "Japanese (日本)", flagIcon = R.drawable.flag_ja, isSelected = false),
            Language(languageCode = "kk", languageName = "Kazakh (қазақ тілі)", flagIcon = R.drawable.flag_kk, isSelected = false),
            Language(languageCode = "kn", languageName = "Kannada (ಕನ್ನಡ)", flagIcon = R.drawable.flag_kn, isSelected = false),
            Language(languageCode = "ko", languageName = "Korean (한국인)", flagIcon = R.drawable.flag_ko, isSelected = false),
            Language(languageCode = "ku", languageName = "Kurdish (کوردی)", flagIcon = R.drawable.flag_ku, isSelected = false),
            Language(languageCode = "la", languageName = "Latin", flagIcon = R.drawable.flag_la, isSelected = false),
            Language(languageCode = "ml", languageName = "Malayalam (മലയാളം)", flagIcon = R.drawable.flag_ml, isSelected = false),
            Language(languageCode = "mr", languageName = "Marathi (मराठी)", flagIcon = R.drawable.flag_mr, isSelected = false),
            Language(languageCode = "ms", languageName = "Malay", flagIcon = R.drawable.flag_ms, isSelected = false),
            Language(languageCode = "nl", languageName = "Dutch (Nederlands)", flagIcon = R.drawable.flag_nl, isSelected = false),
            Language(languageCode = "or", languageName = "Odia (ଓଡ଼ିଆ)", flagIcon = R.drawable.flag_or, isSelected = false),
            Language(languageCode = "pa", languageName = "Punjabi (ਪੰਜਾਬੀ)", flagIcon = R.drawable.flag_pa, isSelected = false),
            Language(languageCode = "pl", languageName = "Polish (Polski)", flagIcon = R.drawable.flag_pl, isSelected = false),
            Language(languageCode = "ps", languageName = "Pashto (پښتو)", flagIcon = R.drawable.flag_ps, isSelected = false),
            Language(languageCode = "pt", languageName = "Portuguese", flagIcon = R.drawable.flag_pt, isSelected = false),
            Language(languageCode = "ru", languageName = "Russian (Русский)", flagIcon = R.drawable.flag_ru, isSelected = false),
            Language(languageCode = "sk", languageName = "Slovak (slovenčina)", flagIcon = R.drawable.flag_sk, isSelected = false),
            Language(languageCode = "sl", languageName = "Slovenian (slovenščina)", flagIcon = R.drawable.flag_sl, isSelected = false),
            Language(languageCode = "sv", languageName = "Swedish (svenska)", flagIcon = R.drawable.flag_sv, isSelected = false),
            Language(languageCode = "te", languageName = "Telugu (తెలుగు)", flagIcon = R.drawable.flag_te, isSelected = false),
            Language(languageCode = "th", languageName = "Thai (ไทย)", flagIcon = R.drawable.flag_th, isSelected = false),
            Language(languageCode = "tr", languageName = "Turkish (Türkçe)", flagIcon = R.drawable.flag_tr, isSelected = false),
            Language(languageCode = "uk", languageName = "Ukrainian (український)", flagIcon = R.drawable.flag_uk, isSelected = false),
            Language(languageCode = "ur", languageName = "Urdu (اردو)", flagIcon = R.drawable.flag_ur, isSelected = false),
            Language(languageCode = "vi", languageName = "Vietnamese (Tiếng Việt)", flagIcon = R.drawable.flag_vi, isSelected = false),
        )
    }
}