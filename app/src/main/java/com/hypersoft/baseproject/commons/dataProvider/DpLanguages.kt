package com.hypersoft.baseproject.commons.dataProvider

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.commons.models.LanguageItem
import com.hypersoft.baseproject.helpers.extensions.Extensions.isValidPosition

class DpLanguages {

    fun getLanguagesList(languageCode: String = ""): List<LanguageItem> {
        val arrayList = ArrayList<LanguageItem>().apply {
            add(LanguageItem(languageCode = "en", languageName = "English", countryFlag = R.drawable.flag_en, false))
            add(LanguageItem(languageCode = "af", languageName = "Afrikaans", countryFlag = R.drawable.flag_af, false))
            add(LanguageItem(languageCode = "ar", languageName = "Arabic (عربي)", countryFlag = R.drawable.flag_ar, false))
            add(LanguageItem(languageCode = "az", languageName = "Azerbaijani (azərbaycan dili)", countryFlag = R.drawable.flag_az, false))
            add(LanguageItem(languageCode = "be", languageName = "Belarusian (беларуская мова)", countryFlag = R.drawable.flag_be, false))
            add(LanguageItem(languageCode = "bg", languageName = "Bulgarian (български език)", countryFlag = R.drawable.flag_bg, false))
            add(LanguageItem(languageCode = "bs", languageName = "Bosnian (bosanski jezik)", countryFlag = R.drawable.flag_bs, false))
            add(LanguageItem(languageCode = "ca", languageName = "Catalan (català)", countryFlag = R.drawable.flag_ca, false))
            add(LanguageItem(languageCode = "cs", languageName = "Czech (čeština)", countryFlag = R.drawable.flag_cs, false))
            add(LanguageItem(languageCode = "da", languageName = "Danish (dansk)", countryFlag = R.drawable.flag_da, false))
            add(LanguageItem(languageCode = "de", languageName = "German (Deutsch)", countryFlag = R.drawable.flag_de, false))
            add(LanguageItem(languageCode = "el", languageName = "Greek (ελληνικά)", countryFlag = R.drawable.flag_el, false))
            add(LanguageItem(languageCode = "es", languageName = "Spanish (Española)", countryFlag = R.drawable.flag_es, false))
            add(LanguageItem(languageCode = "et", languageName = "Estonian (eesti keel)", countryFlag = R.drawable.flag_et, false))
            add(LanguageItem(languageCode = "eu", languageName = "Basque (euskara)", countryFlag = R.drawable.flag_eu, false))
            add(LanguageItem(languageCode = "fa", languageName = "Persian (فارسي)", countryFlag = R.drawable.flag_fa, false))
            add(LanguageItem(languageCode = "fi", languageName = "Finnish (Suomalainen)", countryFlag = R.drawable.flag_fi, false))
            add(LanguageItem(languageCode = "fr", languageName = "French (Français)", countryFlag = R.drawable.flag_fr, false))
            add(LanguageItem(languageCode = "gu", languageName = "Gujarati (ગુજરાતી)", countryFlag = R.drawable.flag_gu, false))
            add(LanguageItem(languageCode = "he", languageName = "Hebrew (עִבְרִית)", countryFlag = R.drawable.flag_he, false))
            add(LanguageItem(languageCode = "hi", languageName = "Hindi (हिन्दी)", countryFlag = R.drawable.flag_hi, false))
            add(LanguageItem(languageCode = "hr", languageName = "Croatian (hrvatski jezik)", countryFlag = R.drawable.flag_hr, false))
            add(LanguageItem(languageCode = "hu", languageName = "Hungarian (magyar)", countryFlag = R.drawable.flag_hu, false))
            add(LanguageItem(languageCode = "hy", languageName = "Armenian (հայերեն)", countryFlag = R.drawable.flag_hy, false))
            add(LanguageItem(languageCode = "is", languageName = "Icelandic (íslenska)", countryFlag = R.drawable.flag_is, false))
            add(LanguageItem(languageCode = "it", languageName = "Italian", countryFlag = R.drawable.flag_it, false))
            add(LanguageItem(languageCode = "ja", languageName = "Japanese (日本)", countryFlag = R.drawable.flag_ja, false))
            add(LanguageItem(languageCode = "kk", languageName = "Kazakh (қазақ тілі)", countryFlag = R.drawable.flag_kk, false))
            add(LanguageItem(languageCode = "kn", languageName = "Kannada (ಕನ್ನಡ)", countryFlag = R.drawable.flag_kn, false))
            add(LanguageItem(languageCode = "ko", languageName = "Korean (한국인)", countryFlag = R.drawable.flag_ko, false))
            add(LanguageItem(languageCode = "ku", languageName = "Kurdish (کوردی)", countryFlag = R.drawable.flag_ku, false))
            add(LanguageItem(languageCode = "la", languageName = "Latin", countryFlag = R.drawable.flag_la, false))
            add(LanguageItem(languageCode = "ml", languageName = "Malayalam (മലയാളം)", countryFlag = R.drawable.flag_ml, false))
            add(LanguageItem(languageCode = "mr", languageName = "Marathi (मराठी)", countryFlag = R.drawable.flag_mr, false))
            add(LanguageItem(languageCode = "ms", languageName = "Malay", countryFlag = R.drawable.flag_ms, false))
            add(LanguageItem(languageCode = "nl", languageName = "Dutch (Nederlands)", countryFlag = R.drawable.flag_nl, false))
            add(LanguageItem(languageCode = "or", languageName = "Odia (ଓଡ଼ିଆ)", countryFlag = R.drawable.flag_or, false))
            add(LanguageItem(languageCode = "pa", languageName = "Punjabi (ਪੰਜਾਬੀ)", countryFlag = R.drawable.flag_pa, false))
            add(LanguageItem(languageCode = "pl", languageName = "Polish (Polski)", countryFlag = R.drawable.flag_pl, false))
            add(LanguageItem(languageCode = "ps", languageName = "Pashto (پښتو)", countryFlag = R.drawable.flag_ps, false))
            add(LanguageItem(languageCode = "pt", languageName = "Portuguese", countryFlag = R.drawable.flag_pt, false))
            add(LanguageItem(languageCode = "ru", languageName = "Russian (Русский)", countryFlag = R.drawable.flag_ru, false))
            add(LanguageItem(languageCode = "sk", languageName = "Slovak (slovenčina)", countryFlag = R.drawable.flag_sk, false))
            add(LanguageItem(languageCode = "sl", languageName = "Slovenian (slovenščina)", countryFlag = R.drawable.flag_sl, false))
            add(LanguageItem(languageCode = "sv", languageName = "Swedish (svenska)", countryFlag = R.drawable.flag_sv, false))
            add(LanguageItem(languageCode = "te", languageName = "Telugu (తెలుగు)", countryFlag = R.drawable.flag_te, false))
            add(LanguageItem(languageCode = "th", languageName = "Thai (ไทย)", countryFlag = R.drawable.flag_th, false))
            add(LanguageItem(languageCode = "tr", languageName = "Turkish (Türkçe)", countryFlag = R.drawable.flag_tr, false))
            add(LanguageItem(languageCode = "uk", languageName = "Ukrainian (український)", countryFlag = R.drawable.flag_uk, false))
            add(LanguageItem(languageCode = "ur", languageName = "Urdu (اردو)", countryFlag = R.drawable.flag_ur, false))
            add(LanguageItem(languageCode = "vi", languageName = "Vietnamese (Tiếng Việt)", countryFlag = R.drawable.flag_vi, false))
        }

        val indexOf = arrayList.indexOfFirst { it.languageCode == languageCode }

        if (indexOf.isValidPosition(arrayList))
            arrayList[indexOf].isSelected = true
        else
            arrayList[0].isSelected = true

        return arrayList.toList()
    }

}