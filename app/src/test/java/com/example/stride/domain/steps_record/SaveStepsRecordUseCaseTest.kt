package com.example.stride.domain.steps_record

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.stride.RxTestSchedulerRule
import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.services.StepsRecordService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import java.lang.RuntimeException

class SaveStepsRecordUseCaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var sut: SaveStepsRecordUseCase

    private val stepsRecordService = mock<StepsRecordService>()

    private val stepsRecordDao = mock<StepsRecordDao>()

    private val record = StepsRecord("",0)

    @Before
    fun setUp() {
        sut = SaveStepsRecordUseCase(stepsRecordService, stepsRecordDao)
    }

    @Test
    fun `Given record is successfully saved, when saveRecord is called, then the returned observable emits success`(){
        //GIVEN
        whenever(stepsRecordService.addNewRecord(record))
            .thenReturn(Single.just(Result.Success(true)))
        whenever(stepsRecordDao.save(record))
            .thenReturn(Completable.complete())
        val testObserver = TestObserver<Any>()

        //WHEN
        sut.saveRecord(record).subscribe(testObserver)

        //THEN
        testObserver
            .assertNoErrors()
            .assertResult(true)

        verify(stepsRecordDao).save(record)

    }

    @Test
    fun `Given a save record fails, when stepsService saveRecord is called, then emit the error which caused the failure`(){
        val testError = "Failed to save"
        //GIVEN
        whenever(stepsRecordService.addNewRecord(record))
            .thenReturn(Single.error(RuntimeException(testError)))
        val testObserver = TestObserver<Any>()

        //WHEN
        sut.saveRecord(record).subscribe(testObserver)

        //THEN
        testObserver.assertErrorMessage(testError)
        verify(stepsRecordService, only()).addNewRecord(record)
        verify(stepsRecordDao, never()).save(record)
    }
}