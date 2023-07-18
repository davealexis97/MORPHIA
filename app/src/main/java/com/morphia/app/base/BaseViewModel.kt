package com.morphia.app.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val error: MutableLiveData<String> = MutableLiveData()

    fun getErrorObserver(): MutableLiveData<String> {
        return error
    }

    fun setError(error: String?) {
        this.error.value = error
    }
}