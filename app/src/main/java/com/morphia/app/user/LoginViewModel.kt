package com.morphia.app.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.morphia.app.base.BaseViewModel
import com.morphia.app.base.Resource
import com.morphia.app.modal.UserDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {

    var repository: AuthRepository = AuthRepository()


    val googleSignInHandle: MutableLiveData<Resource<UserDataModel>> = MutableLiveData()
    val authSignInHandle: MutableLiveData<Resource<UserDataModel>> = MutableLiveData()


    fun signInWithGoogle(authCredential: AuthCredential) {
        viewModelScope.launch {
            repository.signInWithGoogle(authCredential)
                .onEach { state ->
                    googleSignInHandle.value = state
                }
                .launchIn(viewModelScope)
        }
    }

    fun signInwithCredential(email: String, password: String) {
        viewModelScope.launch {
            repository.signInWithCredential(email,password)
                .onEach { state ->
                    authSignInHandle.value = state
                }
                .launchIn(viewModelScope)
        }
    }

}