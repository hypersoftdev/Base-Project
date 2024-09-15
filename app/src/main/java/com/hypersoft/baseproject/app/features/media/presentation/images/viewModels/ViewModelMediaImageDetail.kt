package com.hypersoft.baseproject.app.features.media.presentation.images.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.app.features.media.domain.images.entities.ItemMediaImagePhoto
import com.hypersoft.baseproject.app.features.media.domain.images.useCases.UseCaseMediaImageDetail
import com.hypersoft.baseproject.utilities.observers.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *   Developer: Sohaib Ahmed
 *   Date: 9/15/2024
 *   Profile:
 *     -> github.com/epegasus
 *     -> linkedin.com/in/epegasus
 */

class ViewModelMediaImageDetail(private val useCaseMediaImageDetail: UseCaseMediaImageDetail) : ViewModel() {

    private val _imagesLiveData = MutableLiveData<List<ItemMediaImagePhoto>>()
    val imagesLiveData: LiveData<List<ItemMediaImagePhoto>> get() = _imagesLiveData

    private val _navigateScreenLiveData = SingleLiveEvent<String>()
    val navigateScreenLiveData: LiveData<String> get() = _navigateScreenLiveData

    private val _refreshLiveData = SingleLiveEvent<Boolean>()
    val refreshLiveData: LiveData<Boolean> get() = _refreshLiveData

    private val _errorLiveData = SingleLiveEvent<Int>()
    val errorLiveData: LiveData<Int> get() = _errorLiveData

    fun getImages(folderName: String) = viewModelScope.launch(Dispatchers.IO) {
        useCaseMediaImageDetail.getImages(folderName)?.let { list ->
            _imagesLiveData.postValue(list)
        } ?: run {
            _errorLiveData.postValue(R.string.something_went_wrong)
        }
    }

    fun imageClick(imageUri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        useCaseMediaImageDetail.doesUriExist(imageUri)?.let { isExist ->
            if (isExist) {
                _navigateScreenLiveData.postValue(imageUri.toString())
                return@launch
            }
            _errorLiveData.postValue(R.string.image_not_found)
            _refreshLiveData.postValue(true)
        } ?: run {
            _errorLiveData.postValue(R.string.something_went_wrong)
        }
    }
}