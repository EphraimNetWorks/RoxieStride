package com.example.stride.domain.steps_record

import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.response.StepsRecordResponse
import com.example.stride.data.remote.services.StepsRecordService
import com.example.stride.domain.BaseUseCase
import com.example.stride.utils.Optional
import com.example.stride.utils.asOptional
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject

class GetTodayRecordUseCase @Inject constructor(
    private val stepsRecordService: StepsRecordService,
    private val stepsDao: StepsRecordDao
): BaseUseCase() {

    fun getTodaysRecord(): Observable<Optional<StepsRecord>> {

        val todaysDate = DateTimeFormatter.ISO_DATE.format(LocalDate.now())

        return Observable.merge(
                stepsDao.getDayStepsRecord(todaysDate).toObservable(),
                stepsRecordService.getAllStepsRecords().toObservable()
        )
                .map {
                    when(it){
                        is StepsRecord -> {
                            it.asOptional()
                        }
                        is Result.Success<*> -> {
                            val data = (it.data as StepsRecordResponse).data ?: listOf()
                            stepsDao.save(*data.toTypedArray())
                                    .subscribeOn(Schedulers.io())
                                    .doOnComplete {
                                        Timber.d("${data.size} Records successfully saved")
                                    }
                                    .doOnError(Timber::e)
                                    .subscribe()
                            val todaysRecord = data.find { record -> record.day == todaysDate }
                            todaysRecord?.asOptional() ?: Optional(null)
                        }
                        else -> Optional(null)
                    }
                }
    }
}