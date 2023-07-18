package com.morphia.app.user

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.morphia.app.base.APIException
import com.morphia.app.base.Resource
import com.morphia.app.modal.UserDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithGoogle(authCredential: AuthCredential): Flow<Resource<UserDataModel>> =
        flow {

            emit(Resource.loading())
            try {
                val result = firebaseAuth.signInWithCredential(authCredential).await()
                if (result != null) {
                    val isNewUser: Boolean = result.additionalUserInfo?.isNewUser == true
                    if (!isNewUser) {
                        val firebaseUser = firebaseAuth.currentUser
                        if (firebaseUser != null) {
                            val uid = firebaseUser.uid
                            val name = firebaseUser.displayName
                            val email = firebaseUser.email
                            val user = UserDataModel(uid, name, email)

                            emit(Resource.success(user))

                        } else {
                            emit(Resource.error(APIException("something went wrong")))
                        }
                    } else {
                        // new user
                    }
                } else {
                    emit(Resource.error(APIException("something went wrong")))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.error(e))
            }


        }


    fun signInWithCredential(
        email: String,
        password: String,

        ): Flow<Resource<UserDataModel>> = flow {

        emit(Resource.loading())

        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result != null) {
                val isNewUser: Boolean = result.additionalUserInfo?.isNewUser == true
                if (!isNewUser) {
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName
                        val email = firebaseUser.email
                        val user = UserDataModel(uid, name, email)

                        emit(Resource.success(user))

                    } else {
                        emit(Resource.error(APIException("something went wrong")))
                    }
                } else {
                    // new user
                }
            } else {
                emit(Resource.error(APIException("something went wrong")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.error(e))
        }


    }


    fun createUserWithCredential(
        password: String,
        data: UserDataModel
    ): Flow<Resource<UserDataModel>> = flow {

        emit(Resource.loading())

        try {
            val result =
                firebaseAuth.createUserWithEmailAndPassword(data.email.toString(), password)
                    .await()
            if (result != null) {

                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val uploadTask = uploadImage(data)
                    if (uploadTask != null) {
                        val uploadResult = uploadTask.await()
                        if (uploadResult != null) {
                            data.user_image = uploadResult.storage.downloadUrl.await().toString()
                            saveDataToFirebase(data)
                            emit(Resource.success(data))

                        }

                    }

                } else {
                    // new user
                }
            } else {
                emit(Resource.error(APIException("something went wrong")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.error(e))
        }


    }

    private fun saveDataToFirebase(data: UserDataModel) {
        val database = Firebase.database.reference
        database.child("users").child(data.uid).setValue(data)

    }


    private fun uploadImage(data: UserDataModel): UploadTask? {
        try {
            val storage = Firebase.storage
            val storageRef = storage.reference
            val file = Uri.fromFile(File(data.user_image))
            val profileRef = storageRef.child("profiles/${file.lastPathSegment}")
            return profileRef.putFile(file)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

        }
        return null
    }


}