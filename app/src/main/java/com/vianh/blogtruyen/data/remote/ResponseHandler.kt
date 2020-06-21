package com.vianh.blogtruyen.data.remote

interface ResponseHandler<in T> {
    fun onSuccess(result: T)
    fun onError(message: String, code: Int)
}