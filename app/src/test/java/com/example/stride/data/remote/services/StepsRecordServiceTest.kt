package com.example.stride.data.remote.services

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.stride.RxTestSchedulerRule
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.data.remote.Result
import com.example.stride.data.remote.api.StepsRecordApi
import com.example.stride.data.remote.response.StepsRecordResponse
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before


import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class StepsRecordServiceTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var sut: StepsRecordService

    private val context = mock<Context>()
    private val stepsApi = spy(Retrofit.Builder()
        .baseUrl("https://api.mocki.io/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(StepsRecordApi::class.java))

    @Before
    fun setUp() {
        sut = StepsRecordService(context, stepsApi)
    }

    @Test
    fun `Given api call is successful, when get all records call is made, then emit success result with response data`(){

        //GIVEN
        val testResponse = StepsRecordResponse(listOf(StepsRecord("",0)))
        whenever(stepsApi.fetchStepsRecords()).thenReturn(Single.just(retrofit2.Response.success(testResponse)))
        val testObserver = TestObserver<Result<StepsRecordResponse>>()

        //WHEN
        sut.getAllStepsRecords().subscribe(testObserver)

        //THEN
        verify(stepsApi, only()).fetchStepsRecords()
        testObserver.assertNoErrors()
            .assertOf {
                Assert.assertTrue(it.values()[0] is Result.Success && (it.values()[0] as Result.Success).data == testResponse)
            }

    }

    @Test
    fun `Given api call failed, when get all records call is made, then emit error result`(){

        //GIVEN
        whenever(stepsApi.fetchStepsRecords())
            .thenReturn(
                Single.just(
                    retrofit2.Response.error(
                        400,
                        ResponseBody.create(MediaType.parse(""),"")
                    )
                )
            )
        val testObserver = TestObserver<Result<StepsRecordResponse>>()

        //WHEN
        sut.getAllStepsRecords().subscribe(testObserver)

        //THEN
        verify(stepsApi, only()).fetchStepsRecords()
        testObserver
            .assertOf {
                Assert.assertTrue(it.values()[0] is Result.Error)
            }

    }

    @Test
    fun `Given api call is successful, when save record call is made, then emit success result with response data`(){

        //GIVEN
        val testRecord = StepsRecord("",0)
        whenever(stepsApi.addNewRecord(testRecord)).thenReturn(Single.just(retrofit2.Response.success(true)))
        val testObserver = TestObserver<Result<Any>>()

        //WHEN
        sut.addNewRecord(testRecord).subscribe(testObserver)

        //THEN
        verify(stepsApi, only()).addNewRecord(testRecord)
        testObserver.assertNoErrors()
            .assertOf {
                Assert.assertTrue(it.values()[0] is Result.Success && (it.values()[0] as Result.Success).data == true)
            }

    }

    @Test
    fun `Given api call failed, when save record call is made, then emit error result`(){

        //GIVEN
        val testRecord = StepsRecord("",0)
        whenever(stepsApi.addNewRecord(testRecord))
            .thenReturn(
                Single.just(
                    retrofit2.Response.error(
                        400,
                        ResponseBody.create(MediaType.parse(""),"")
                    )
                )
            )
        val testObserver = TestObserver<Result<Any>>()

        //WHEN
        sut.addNewRecord(testRecord).subscribe(testObserver)

        //THEN
        verify(stepsApi, only()).addNewRecord(testRecord)

        testObserver
            .assertOf {
                Assert.assertTrue(it.values()[0] is Result.Error)
            }

    }

    @Test
    fun `Given api call is successful, when delete record call is made, then emit success result with response data`(){

        //GIVEN
        val testRecord = StepsRecord("",0)
        whenever(stepsApi.deleteRecord(testRecord.day)).thenReturn(Single.just(retrofit2.Response.success(true)))
        val testObserver = TestObserver<Result<Any>>()

        //WHEN
        sut.deleteRecord(testRecord).subscribe(testObserver)

        //THEN
        verify(stepsApi, only()).deleteRecord(testRecord.day)
        testObserver.assertNoErrors()
            .assertOf {
                Assert.assertTrue(it.values()[0] is Result.Success && (it.values()[0] as Result.Success).data == true)
            }

    }

    @Test
    fun `Given api call failed, when delete record call is made, then emit error result`(){

        //GIVEN
        val testRecord = StepsRecord("",0)
        whenever(stepsApi.deleteRecord(testRecord.day))
            .thenReturn(
                Single.just(
                    retrofit2.Response.error(
                        400,
                        ResponseBody.create(MediaType.parse(""),"")
                    )
                )
            )
        val testObserver = TestObserver<Result<Any>>()

        //WHEN
        sut.deleteRecord(testRecord).subscribe(testObserver)

        //THEN
        verify(stepsApi, only()).deleteRecord(testRecord.day)

        testObserver
            .assertOf {
                Assert.assertTrue(it.values()[0] is Result.Error)
            }

    }

    @Test
    fun `Given api call is successful, when update record call is made, then emit success result with response data`(){

        //GIVEN
        val testRecord = StepsRecord("",0)
        whenever(stepsApi.updateStepsRecord(testRecord)).thenReturn(Single.just(retrofit2.Response.success(true)))
        val testObserver = TestObserver<Result<Any>>()

        //WHEN
        sut.updateRecord(testRecord).subscribe(testObserver)

        //THEN
        verify(stepsApi, only()).updateStepsRecord(testRecord)
        testObserver.assertNoErrors()
            .assertOf {
                Assert.assertTrue(it.values()[0] is Result.Success && (it.values()[0] as Result.Success).data == true)
            }

    }

    @Test
    fun `Given api call failed, when update record call is made, then emit error result`(){

        //GIVEN
        val testRecord = StepsRecord("",0)
        whenever(stepsApi.updateStepsRecord(testRecord))
            .thenReturn(
                Single.just(
                    retrofit2.Response.error(
                        400,
                        ResponseBody.create(MediaType.parse(""),"")
                    )
                )
            )
        val testObserver = TestObserver<Result<Any>>()

        //WHEN
        sut.updateRecord(testRecord).subscribe(testObserver)

        //THEN
        verify(stepsApi, only()).updateStepsRecord(testRecord)

        testObserver
            .assertOf {
                Assert.assertTrue(it.values()[0] is Result.Error)
            }

    }

}