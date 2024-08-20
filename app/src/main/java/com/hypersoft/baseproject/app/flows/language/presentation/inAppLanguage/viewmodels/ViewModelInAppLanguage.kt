package com.hypersoft.baseproject.app.flows.language.presentation.inAppLanguage.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hypersoft.baseproject.app.flows.language.domain.entities.ItemLanguage
import com.hypersoft.baseproject.app.flows.language.domain.usecases.UseCaseInAppLanguage

class ViewModelInAppLanguage(private val useCaseInAppLanguage: UseCaseInAppLanguage) : ViewModel() {

    private val _languagesLiveData = MutableLiveData<List<ItemLanguage>>()
    val languageLiveData: LiveData<List<ItemLanguage>> get() = _languagesLiveData

    private val _appliedLiveData = MutableLiveData<Boolean>()
    val appliedLiveData: LiveData<Boolean> get() = _appliedLiveData

    init {
        fetchLanguages()
    }

    private fun fetchLanguages() {
        _languagesLiveData.value = useCaseInAppLanguage.fetchLanguages()
    }

    fun updateLanguage(selectedCode: String) {
        _languagesLiveData.value = useCaseInAppLanguage.updateLanguages(selectedCode)
    }

    fun applyLanguage() {
        val isApplied = useCaseInAppLanguage.applyLanguage()
        _appliedLiveData.value = isApplied
    }
}