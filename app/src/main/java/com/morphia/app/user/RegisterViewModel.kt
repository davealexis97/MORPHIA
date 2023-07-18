package com.morphia.app.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.UploadTask
import com.morphia.app.base.BaseViewModel
import com.morphia.app.base.Resource
import com.morphia.app.modal.UserDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RegisterViewModel : BaseViewModel() {

    var repository: AuthRepository = AuthRepository()

    val authSignUpHandle: MutableLiveData<Resource<UserDataModel>> = MutableLiveData()
    val profileImageUpload: MutableLiveData<Resource<String>> = MutableLiveData()

    fun createUserWithCredential(password:String,data: UserDataModel) {
        viewModelScope.launch {
            repository.createUserWithCredential(password, data)
                .onEach { state ->
                    authSignUpHandle.value = state
                }
                .launchIn(viewModelScope)
        }
    }



}