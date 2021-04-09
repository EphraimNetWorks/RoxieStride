package com.example.stride.domain.steps_record

import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.services.StepsRecordService
import com.example.stride.domain.BaseUseCase
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DeleteStepsRecordUseCase @Inject constructor(
    private val stepsRecordService: StepsRecordService,
    private val stepsDao: StepsRecordDao
): BaseUseCase() {

    fun deleteRecord(record:StepsRecord): Observable<Boolean> {

        return stepsRecordService.deleteRecord(record)
            .map {
                when(it){
                    is Result.Success<*> -> {
                        stepsDao.delete(record)
                                .subscribeOn(Schedulers.io())
                                .doOnComplete {
                                    Timber.d("Record successfully deleted")
                                }
                                .doOnError(Timber::e)
                                .subscribe()
                        true
                    }
                    is Result.Error ->{
                        throw it.throwable
                    }
                }
            }.toObservable()
    }

}