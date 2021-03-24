package com.example.stride.data.remote

sealed class Result<T> {
    class Loading<T>:Result<T>()
    data class Success<T>(val data:T):Result<T>()
    data class Error<T>(val throwable: Throwable, val data:T? = null):Result<T>()
}