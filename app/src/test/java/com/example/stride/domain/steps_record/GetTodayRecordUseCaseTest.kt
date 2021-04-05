package com.example.stride.domain.steps_record

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.stride.RxTestSchedulerRule
import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.response.StepsRecordResponse
import com.example.stride.data.remote.services.StepsRecordService
import com.example.stride.utils.Optional
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.lang.RuntimeException

class GetTodayRecordUseCaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var sut: GetTodayRecordUseCase

    private val stepsRecordService = mock<StepsRecordService>()

    private val stepsRecordDao = mock<StepsRecordDao>()

    private val today = DateTimeFormatter.ISO_DATE.format(LocalDate.now())
    private val todaysRecord = StepsRecord("",0)

    @Before
    fun setUp() {
        sut = GetTodayRecordUseCase(stepsRecordService, stepsRecordDao)
    }

    @Test
    fun `Given today's step record successfully loaded from local db, when getTodaysRecord is called, then the returned observable emits today's record`(){
        //GIVEN
        whenever(stepsRecordDao.getDayStepsRecord(today))
            .thenReturn(Flowable.just(todaysRecord))
        whenever(stepsRecordDao.save(any()))
            .thenReturn(Completable.complete())
        whenever(stepsRecordService.getAllStepsRecords())
            .thenReturn(Single.just(Result.Success(StepsRecordResponse(listOf()))))
        val testObserver = TestObserver<Any>()

        //WHEN
        sut.getTodaysRecord().subscribe(testObserver)

        //THEN
        testObserver.assertNoErrors()
        testObserver.assertValueAt(0, Optional(todaysRecord))
        verify(stepsRecordDao).getDayStepsRecord(today)

    }

    @Test
    fun `Given a list of steps record is returned, when stepsService getAllStepsRecord is called, then save the list to local database`(){
        //GIVEN
        whenever(stepsRecordDao.getDayStepsRecord(today))
                .thenReturn(Flowable.just<StepsRecord>(StepsRecord("",0)))
        whenever(stepsRecordService.getAllStepsRecords())
                .thenReturn(Single.just(Result.Success(StepsRecordResponse(listOf(todaysRecord)))))
        val testObserver = TestObserver<Any>()

        //WHEN
        sut.getTodaysRecord().subscribe(testObserver)

        //THEN
        verify(stepsRecordService, only()).getAllStepsRecords()
        verify(stepsRecordDao).save(any())
    }

    @Test
    fun `Given a list of steps records fails to load, when stepsService getAllStepsRecord is called, then emit the error which caused the failure`(){
        val testError = "Failed to load"
        //GIVEN
        whenever(stepsRecordDao.getDayStepsRecord(today))
                .thenReturn(Flowable.error(RuntimeException(testError)))
        whenever(stepsRecordService.getAllStepsRecords())
                .thenReturn(Single.error(RuntimeException(testError)))
        val testObserver = TestObserver<Any>()

        //WHEN
        sut.getTodaysRecord().subscribe(testObserver)

        //THEN
        testObserver.assertErrorMessage(testError)
        verify(stepsRecordService, only()).getAllStepsRecords()
        verify(stepsRecordDao, never()).save(any())
    }
}