package com.example.stride.domain.steps_record

import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.services.StepsRecordService
import com.example.stride.domain.BaseUseCase
import com.example.stride.utils.Optional
import com.example.stride.utils.asOptional
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class GetTodayRecordUseCase @Inject constructor(
    private val stepsRecordService: StepsRecordService,
    private val stepsDao: StepsRecordDao
): BaseUseCase() {

    private val _mostRecentRecordObs = BehaviorSubject.create<Optional<StepsRecord>>()

    fun getTodaysRecord(): Observable<Optional<StepsRecord>> {

        val todaysDate = DateTimeFormatter.ISO_DATE.format(LocalDate.now())
        disposables += stepsDao.getDayStepsRecord(todaysDate)
            .subscribeOn(Schedulers.io())
            .subscribe{ record ->
                _mostRecentRecordObs.onNext(record.asOptional())
            }

        disposables += stepsRecordService.getAllStepsRecords()
            .subscribeOn(Schedulers.io())
            .subscribe ({ result->
                when(result){
                    is Result.Success -> {
                        val data = result.data.data ?: listOf()
                        run{ stepsDao.save(*data.toTypedArray()) }
                        val todaysRecord = data.find { it.day == todaysDate }
                        if (todaysRecord != null){
                            _mostRecentRecordObs.onNext(todaysRecord.asOptional())
                        }else{
                            _mostRecentRecordObs.onNext(Optional(null))
                        }
                    }
                    is Result.Error -> _mostRecentRecordObs.onError(result.throwable)
                }
            }, {err->
                _mostRecentRecordObs.onError(err)
            })

        return  _mostRecentRecordObs
    }
}