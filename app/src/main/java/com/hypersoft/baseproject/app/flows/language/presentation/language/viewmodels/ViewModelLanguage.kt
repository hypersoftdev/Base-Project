package com.hypersoft.baseproject.app.flows.language.presentation.language.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hypersoft.baseproject.app.flows.language.domain.usecases.UseCaseLanguage
import com.hypersoft.baseproject.app.flows.language.domain.entities.ItemLanguage

class ViewModelLanguage(private val useCaseLanguage: UseCaseLanguage) : ViewModel() {

    private val _languagesLiveData = MutableLiveData<List<ItemLanguage>>()
    val languageLiveData: LiveData<List<ItemLanguage>> get() = _languagesLiveData

    private val _appliedLiveData = MutableLiveData<Boolean>()
    val appliedLiveData: LiveData<Boolean> get() = _appliedLiveData

    fun fetchLanguages(appliedLanguageCode: String) {
        _languagesLiveData.value = useCaseLanguage.fetchLanguages(appliedLanguageCode)
    }

    fun updateLanguage(selectedCode: String) {
        _languagesLiveData.value = useCaseLanguage.updateLanguages(selectedCode)
    }

    fun applyLanguage() {
        val isApplied = useCaseLanguage.applyLanguage()
        _appliedLiveData.value = isApplied
    }
}