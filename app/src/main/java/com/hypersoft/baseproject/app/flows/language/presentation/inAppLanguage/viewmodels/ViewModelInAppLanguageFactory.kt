package com.hypersoft.baseproject.app.flows.language.presentation.inAppLanguage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hypersoft.baseproject.app.flows.language.domain.usecases.UseCaseInAppLanguage

class ViewModelInAppLanguageFactory(private val useCaseLanguage: UseCaseInAppLanguage) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelInAppLanguage::class.java)) {
            return ViewModelInAppLanguage(useCaseLanguage) as T
        }
        return super.create(modelClass)
    }
}