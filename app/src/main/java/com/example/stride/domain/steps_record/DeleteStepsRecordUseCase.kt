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

class DeleteStepsRecordUseCase @Inject constructor(
    private val stepsRecordService: StepsRecordService,
    private val stepsDao: StepsRecordDao
): BaseUseCase() {

    private val _deleteRecordObs = PublishSubject.create<Boolean>()

    fun deleteRecord(record:StepsRecord): Observable<Boolean> {

        disposables += stepsRecordService.deleteRecord(record)
            .subscribe{ result->
                when(result){
                    is Result.Success -> {
                        run { stepsDao.delete(record) }
                        _deleteRecordObs.onNext(true)
                    }
                    is Result.Error -> _deleteRecordObs.onError(result.throwable)
                }
            }

        return  _deleteRecordObs
    }

}