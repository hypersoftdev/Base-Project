package com.hypersoft.baseproject.app.flows.language.presentation.language.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hypersoft.baseproject.app.flows.language.domain.usecases.UseCaseLanguage

class ViewModelLanguageFactory(private val useCaseLanguage: UseCaseLanguage) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelLanguage::class.java)) {
            return ViewModelLanguage(useCaseLanguage) as T
        }
        return super.create(modelClass)
    }
}