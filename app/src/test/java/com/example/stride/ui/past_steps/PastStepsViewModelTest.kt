package com.example.stride.ui.past_steps


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.example.stride.RxTestSchedulerRule
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.domain.steps_record.GetStepsRecordUseCase
import com.example.stride.domain.steps_record.GetTodayRecordUseCase
import com.example.stride.utils.Optional
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.*

import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.capture
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class PastStepsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    @Captor
    lateinit var pastStateCaptor: ArgumentCaptor<PastStepsState>

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var sut: PastStepsViewModel

    private val getStepsRecordUseCase = mock<GetStepsRecordUseCase>()
    private val getTodayRecordUseCase = mock<GetTodayRecordUseCase>()
    private val savedStateHandle = mock<SavedStateHandle>()
    private val observer = mock<Observer<PastStepsState>>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = PastStepsViewModel(savedStateHandle, getStepsRecordUseCase, getTodayRecordUseCase)
        Dispatchers.setMain(testCoroutineDispatcher)
        sut.observableState.observeForever(observer)
    }

    @After
    fun tearDown(){
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `Given today's step record successfully loaded, when GetTodaysRecord is received, then State contains today's record exist`(){
        //GIVEN
        val todaysRecord = StepsRecord("",0)
        val successState = PastStepsState(todaysRecord = todaysRecord)
        whenever(getTodayRecordUseCase.getTodaysRecord())
            .thenReturn(Single.just(Optional(todaysRecord)).toObservable())

        //WHEN
        sut.dispatch(PastStepsAction.GetTodaysStepsRecord)
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer).onChanged(successState)
    }


    @Test
    fun `Given today's steps record failed to load, when GetTodaysRecord is received, then State contains error`(){
        //GIVEN
        val errMsg = "Error"
        val errorState = PastStepsState(isError = true, error = errMsg)
        whenever(getTodayRecordUseCase.getTodaysRecord())
            .thenReturn(Single.error<Optional<StepsRecord>>(RuntimeException(errMsg)).toObservable())

        //WHEN
        sut.dispatch(PastStepsAction.GetTodaysStepsRecord)
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer).onChanged(errorState)
    }

    @Test
    fun `Given past step records successfully loaded, when GetPastRecords is received, then State contains past records data`(){
        //GIVEN
        val record = StepsRecord("",0)
        val data = PagingData.from(arrayListOf(record))
        val successState = PastStepsState(pastStepsRecords = data)
        whenever(getStepsRecordUseCase.getRecords(Filter.PAST_YEAR))
            .thenReturn(Single.just(data).toObservable())

        //WHEN
        sut.dispatch(PastStepsAction.GetStepsRecords(Filter.PAST_YEAR))
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer).onChanged(capture(pastStateCaptor))
        val onChangedState = pastStateCaptor.value
        Assert.assertNotNull(onChangedState.pastStepsRecords)
        Assert.assertEquals(onChangedState.isLoading, successState.isLoading)
        Assert.assertEquals(onChangedState.isError, successState.isError)
        Assert.assertEquals(onChangedState.error, successState.error)
        Assert.assertEquals(onChangedState.todaysRecord, successState.todaysRecord)
        Assert.assertEquals(onChangedState.isIdle, successState.isIdle)
    }


    @Test
    fun `Given past steps records failed to load, when GetPastRecords is received, then State contains error`(){
        //GIVEN
        val errMsg = "Error"
        val errorState = PastStepsState(isError = true, error = errMsg)
        whenever(getStepsRecordUseCase.getRecords(Filter.PAST_YEAR))
            .thenReturn(Single.error<PagingData<StepsRecord>>(RuntimeException(errMsg)).toObservable())

        //WHEN
        sut.dispatch(PastStepsAction.GetStepsRecords(Filter.PAST_YEAR))
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer).onChanged(errorState)
    }

}