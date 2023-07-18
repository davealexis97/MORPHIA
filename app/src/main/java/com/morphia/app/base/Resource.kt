package com.morphia.app.base

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val exception: Exception? = null
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(error: Exception): Resource<T> {
            return Resource(Status.ERROR, null, error)
        }

        fun <T> loading(): Resource<T> {
            return Resource(
                Status.LOADING,
                null,
                null
            )

        }


    }
}
