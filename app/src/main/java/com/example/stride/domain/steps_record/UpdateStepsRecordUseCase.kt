package com.example.stride.domain.steps_record

import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.services.StepsRecordService
import com.example.stride.domain.BaseUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class UpdateStepsRecordUseCase @Inject constructor(
    private val stepsRecordService: StepsRecordService,
    private val stepsDao: StepsRecordDao
): BaseUseCase() {

    private val _updateRecordObs = PublishSubject.create<Boolean>()

    fun updateRecord(record: StepsRecord): Observable<Boolean> {

        disposables += stepsRecordService.updateRecord(record)
            .subscribeOn(Schedulers.io())
            .subscribe{ result->
                when(result){
                    is Result.Success -> {
                        run { stepsDao.save(record) }
                        _updateRecordObs.onNext(true)
                    }
                    is Result.Error -> _updateRecordObs.onError(result.throwable)
                }
            }

        return  _updateRecordObs

    }
    
}