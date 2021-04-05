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

class DeleteStepsRecordUseCaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var sut: DeleteStepsRecordUseCase

    private val stepsRecordService = mock<StepsRecordService>()

    private val stepsRecordDao = mock<StepsRecordDao>()

    private val record = StepsRecord("",0)

    @Before
    fun setUp() {
        sut = DeleteStepsRecordUseCase(stepsRecordService, stepsRecordDao)
    }

    @Test
    fun `Given record is successfully deleted, when deleteRecord is called, then the returned observable emits success`(){
        //GIVEN
        whenever(stepsRecordService.deleteRecord(record))
            .thenReturn(Single.just(Result.Success(true)))
        whenever(stepsRecordDao.delete(record))
            .thenReturn(Completable.complete())
        val testObserver = TestObserver<Boolean>()

        //WHEN
        sut.deleteRecord(record).subscribe(testObserver)

        //THEN
        testObserver
            .assertNoErrors()
            .assertResult(true)

        verify(stepsRecordDao).delete(record)

    }

    @Test
    fun `Given a delete record fails, when stepsService deleteRecord is called, then emit the error which caused the failure`(){
        val testError = "Failed to delete"
        //GIVEN
        whenever(stepsRecordService.deleteRecord(record))
            .thenReturn(Single.error(RuntimeException(testError)))
        val testObserver = TestObserver<Any>()

        //WHEN
        sut.deleteRecord(record).subscribe(testObserver)

        //THEN
        testObserver.assertErrorMessage(testError)
        verify(stepsRecordService, only()).deleteRecord(record)
        verify(stepsRecordDao, never()).delete(record)
    }
}