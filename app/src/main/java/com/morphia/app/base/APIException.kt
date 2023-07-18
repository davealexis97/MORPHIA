package com.morphia.app.base

class APIException : Exception {

    constructor(message: String?) : super(message)
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)
}