package com.hypersoft.baseproject.app.flows.language.data.repository

import com.hypersoft.baseproject.app.flows.language.data.datasources.DpLanguage
import com.hypersoft.baseproject.app.flows.language.domain.entities.ItemLanguage

class RepositoryLanguage {

    fun getLanguages(): List<ItemLanguage> {
        val dpLanguage = DpLanguage()
        return dpLanguage.languageList
    }
}