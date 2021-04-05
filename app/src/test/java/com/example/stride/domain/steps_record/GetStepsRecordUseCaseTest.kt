package com.example.stride.domain.steps_record

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.example.stride.MockPagingSource
import com.example.stride.RxTestSchedulerRule
import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.response.StepsRecordResponse
import com.example.stride.data.remote.services.StepsRecordService
import com.example.stride.ui.past_steps.Filter
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class GetStepsRecordUseCaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var sut: GetStepsRecordUseCase

    private val stepsRecordService = mock<StepsRecordService>()

    private val stepsRecordDao = mock<StepsRecordDao>()

    private val record = StepsRecord("",0)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = GetStepsRecordUseCase(stepsRecordService, stepsRecordDao)
    }

    @Test
    fun `Given records is successfully loaded, when getRecords is called, then the returned observable emits paged data of steps records`() {
        //GIVEN
        val testResults = listOf(record)
        whenever(stepsRecordService.getAllStepsRecords())
            .thenReturn(Single.just(Result.Success(StepsRecordResponse(testResults))))
        whenever(stepsRecordDao.getStepRecords(any()))
            .thenReturn(MockPagingSource(listOf(record)))
        val testObserver = TestObserver<PagingData<StepsRecord>>()

        //WHEN
        sut.getRecords(Filter.PAST_YEAR).subscribe(testObserver)

        //THEN
        testObserver
            .assertNoErrors()

        verify(stepsRecordDao).getStepRecords(any())

    }

    @Test
    fun `Given records is successfully loaded, when refreshRecords is called, then the returned observable emits success`(){
        //GIVEN
        whenever(stepsRecordService.getAllStepsRecords())
            .thenReturn(Single.just(Result.Success(StepsRecordResponse(listOf(record)))))
        whenever(stepsRecordDao.save(any()))
            .thenReturn(Completable.complete())
        val testObserver = TestObserver<Boolean>()

        //WHEN
        sut.refreshStepRecords().subscribe(testObserver)

        //THEN
        testObserver.assertResult(true)
        verify(stepsRecordDao, only()).save(any())

    }

    @Test
    fun `Given a records fails to load, when refreshStepRecords is called, then emit the error which caused the failure`(){
        val testError = "Failed to load"
        //GIVEN
        whenever(stepsRecordService.getAllStepsRecords())
            .thenReturn(Single.error(RuntimeException(testError)))
        val testObserver = TestObserver<Any>()

        //WHEN
        sut.refreshStepRecords().subscribe(testObserver)

        //THEN
        testObserver.assertErrorMessage(testError)
        verify(stepsRecordService, only()).getAllStepsRecords()
        verify(stepsRecordDao, never()).save(any())
    }
}