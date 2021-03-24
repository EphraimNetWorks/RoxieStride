package com.example.stride.ui.record_step


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.example.stride.RxTestSchedulerRule
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.domain.steps_record.SaveStepsRecordUseCase
import io.reactivex.Single
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.lang.RuntimeException

class RecordStepsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var sut: RecordStepsViewModel

    private val saveStepsRecordUseCase = mock<SaveStepsRecordUseCase>()
    private val savedStateHandle = mock<SavedStateHandle>()
    private val observer = mock<Observer<RecordStepsState>>()

    @Before
    fun setUp() {
        sut = RecordStepsViewModel(savedStateHandle, saveStepsRecordUseCase)
        sut.observableState.observeForever(observer)
    }

    @Test
    fun `Given new record was successfully saved, when AddNewStepsRecord is received, then State emits record saved`(){
        //GIVEN
        val testRecord = StepsRecord("",0)
        val recordExistState = RecordStepsState(isSuccess = true)
        whenever(saveStepsRecordUseCase.saveRecord(testRecord))
            .thenReturn(Single.just(true).toObservable())

        //WHEN
        sut.dispatch(RecordStepsAction.AddNewStepsRecord(testRecord))
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer).onChanged(recordExistState)
        verifyNoMoreInteractions(observer)
    }


    @Test
    fun `Given new record failed to save, when AddNewStepsRecord is received, then State contains error`(){
        //GIVEN
        val testRecord = StepsRecord("",0)
        val errMsg = "Error"
        val errorState = RecordStepsState(isError = true, error = errMsg)
        whenever(saveStepsRecordUseCase.saveRecord(testRecord))
            .thenReturn(Single.error<Boolean>(RuntimeException(errMsg)).toObservable())

        //WHEN
        sut.dispatch(RecordStepsAction.AddNewStepsRecord(testRecord))
        testSchedulerRule.triggerActions()

        //THEN
        verify(observer).onChanged(errorState)
        verifyNoMoreInteractions(observer)
    }

}