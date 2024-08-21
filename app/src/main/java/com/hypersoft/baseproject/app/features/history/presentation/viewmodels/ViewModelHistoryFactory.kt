package com.hypersoft.baseproject.app.features.history.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hypersoft.baseproject.app.features.history.domain.usecases.UseCaseHistory

class ViewModelHistoryFactory(private val useCaseLanguage: UseCaseHistory) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelHistory::class.java)) {
            return ViewModelHistory(useCaseLanguage) as T
        }
        return super.create(modelClass)
    }
}