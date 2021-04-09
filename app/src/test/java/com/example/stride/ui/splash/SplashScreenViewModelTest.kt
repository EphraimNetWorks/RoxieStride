package com.example.stride.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.example.stride.RxTestSchedulerRule
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.domain.steps_record.GetTodayRecordUseCase
import com.example.stride.utils.Optional
import io.reactivex.Single
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import java.lang.RuntimeException

class SplashScreenViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var sut:SplashScreenViewModel

    private val getTodayRecordUseCase = mock<GetTodayRecordUseCase>()
    private val savedStateHandle = mock<SavedStateHandle>()
    private val observer = mock<Observer<SplashState>>()

    @Before
    fun setUp() {
        sut = SplashScreenViewModel(savedStateHandle, getTodayRecordUseCase)
        sut.observableState.observeForever(observer)
    }

    @Test
    fun `Given today's step record successfully loaded, when GetTodaysRecord is received, then State contains today's record exist`(){
        //GIVEN
        val todaysRecord = StepsRecord("",0)
        val recordExistState = SplashState(isMostRecentRecordToday = true)
        whenever(getTodayRecordUseCase.getTodaysRecord())
            .thenReturn(Single.just(Optional(todaysRecord)).toObservable())

        //WHEN
        sut.dispatch(SplashAction.CheckForTodaysStepRecord)
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer, timeout(5000)).onChanged(recordExistState)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `Given today's doesn't exist, when GetTodaysRecord is received, then State contains today's record doesn't exist`(){
        //GIVEN
        val absentRecordState = SplashState(isMostRecentRecordToday = false)
        whenever(getTodayRecordUseCase.getTodaysRecord())
            .thenReturn(Single.just<Optional<StepsRecord>>(Optional(null)).toObservable())

        //WHEN
        sut.dispatch(SplashAction.CheckForTodaysStepRecord)
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer).onChanged(absentRecordState)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `Given today's steps record failed to load, when GetTodaysRecord is received, then State contains error`(){
        //GIVEN
        val errMsg = "Error"
        val errorState = SplashState(isError = true, error = errMsg)
        whenever(getTodayRecordUseCase.getTodaysRecord())
            .thenReturn(Single.error<Optional<StepsRecord>>(RuntimeException(errMsg)).toObservable())

        //WHEN
        sut.dispatch(SplashAction.CheckForTodaysStepRecord)
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer).onChanged(errorState)
        verifyNoMoreInteractions(observer)
    }

}