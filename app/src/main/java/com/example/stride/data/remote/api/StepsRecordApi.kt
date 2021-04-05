package com.example.stride.data.remote.api

import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.response.StepsRecordResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface StepsRecordApi {

    @GET("v1/88ed478f/")
    fun fetchStepsRecords(): Single<Response<StepsRecordResponse>>

    @DELETE("v1/88ed478f/remove-record")
    fun deleteRecord(@Query("recordDay") recordDay: String):Single<Response<Any>>

    @POST("v1/88ed478f/new-record")
    fun addNewRecord(@Body newRecord: StepsRecord):Single<Response<Any>>

    @PUT("v1/88ed478f/edit-record")
    fun updateStepsRecord(@Body updatedRecord: StepsRecord):Single<Response<Any>>

}