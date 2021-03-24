package com.example.stride.data.remote.services

import android.content.Context
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.api.StepsRecordApi
import com.example.stride.data.remote.response.StepsRecordResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Single
import javax.inject.Inject

class StepsRecordService @Inject constructor(
    @ApplicationContext context: Context,
    private val stepsRecordApi: StepsRecordApi
): BaseService(context){

    fun getAllStepsRecords(): Single<Result<StepsRecordResponse>> {
        return stepsRecordApi.fetchStepsRecords().perform()
    }

    fun addNewRecord(newRecord:StepsRecord):Single<Result<Any>>{
        return stepsRecordApi.addNewRecord(newRecord).perform()
    }

    fun deleteRecord(record:StepsRecord):Single<Result<Any>>{
        return stepsRecordApi.deleteRecord(record.day).perform()
    }

    fun updateRecord(updatedRecord:StepsRecord):Single<Result<Any>>{
        return stepsRecordApi.updateStepsRecord(updatedRecord).perform()
    }
}