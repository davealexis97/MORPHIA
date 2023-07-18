package com.morphia.app.modal

import com.google.gson.annotations.SerializedName

data class UserDataModel(
    @SerializedName("uid")
    var uid: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("organization")
    val organization: String? = null,
    @SerializedName("user_image")
    var user_image: String? = null
)
