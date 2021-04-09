package com.example.stride.domain.steps_record

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.observable
import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.services.StepsRecordService
import com.example.stride.domain.BaseUseCase
import com.example.stride.ui.past_steps.Filter
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import javax.inject.Inject

class GetStepsRecordUseCase @Inject constructor(
    private val stepsRecordService: StepsRecordService,
    private val stepsDao: StepsRecordDao
): BaseUseCase() {

    @ExperimentalCoroutinesApi
    fun getRecords(filter: Filter):Observable<PagingData<StepsRecord>>{

        val fromDate = filterToFromDate(filter)

        disposables += refreshStepRecords().doOnError { Timber.e(it) }.subscribeOn(Schedulers.io()).subscribe()

        return  Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 30,
                prefetchDistance = 5,
                initialLoadSize = 20),
            pagingSourceFactory = { stepsDao.getStepRecords(fromDate) }
        ).observable
    }

    fun refreshStepRecords():Observable<Boolean>{
        return stepsRecordService.getAllStepsRecords()
            .map {result->
                when(result){
                    is Result.Success -> {
                        val data = result.data.data ?: listOf()
                        stepsDao.save(*data.toTypedArray())
                                .subscribeOn(Schedulers.io())
                                .doOnComplete {
                                    Timber.d("${data.size} Records successfully saved")
                                }
                                .doOnError(Timber::e)
                                .subscribe()
                        true
                    }
                    is Result.Error -> throw result.throwable
                }
            }.toObservable()
    }

    private fun filterToFromDate(filter: Filter):String{
        val daysToSubstract = when(filter){
            Filter.PAST_3_DAYS-> 3
            Filter.PAST_WEEK-> 7
            Filter.PAST_MONTH-> 30
            Filter.PAST_YEAR-> 366
        }.toLong()
        val dateTime  = LocalDate.now().minus(daysToSubstract,ChronoUnit.DAYS)

        return DateTimeFormatter.ISO_DATE.format(dateTime)
    }
}