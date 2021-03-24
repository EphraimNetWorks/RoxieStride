package com.example.stride.data.remote.services

import android.content.Context
import com.example.stride.data.remote.Result
import io.reactivex.Single
import retrofit2.Response

abstract class BaseService(protected val context: Context) {

    protected fun <T> Single<Response<T>>.perform():Single<Result<T>>{
        return this.map<Result<T>> {
            if (it.isSuccessful){
                if (it.body() != null){
                    return@map Result.Success(it.body()!!)
                }else{
                    return@map Result.Error(Throwable(it.message()))
                }
            }else{
                return@map Result.Error(Throwable(it.message()))
            }
        }.onErrorReturn {
            return@onErrorReturn Result.Error<T>(it)
        }
    }

}