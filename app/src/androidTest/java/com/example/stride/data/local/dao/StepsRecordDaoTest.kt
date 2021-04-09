package com.example.stride.data.local.dao

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stride.RxTestSchedulerRule
import com.example.stride.data.local.AppDatabase
import com.example.stride.data.local.entity.StepsRecord
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class StepsRecordDaoTest {
    private lateinit var stepsRecordDao: StepsRecordDao
    private lateinit var db: AppDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()


    private val testRecord = StepsRecord("1900-8-7",100)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).allowMainThreadQueries().build()
        stepsRecordDao = db.stepRecordDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun given_days_step_record_successfully_saved_When_save_is_received_Then_record_should_exist_in_database() {
        //GIVEN
        val testObserver = TestObserver<StepsRecord>()

        //WHEN
        stepsRecordDao.save(testRecord).subscribe()

        //THEN
        stepsRecordDao.getDayStepsRecord(testRecord.day).toObservable().subscribe(testObserver)
        testObserver.assertNoErrors().assertValueAt(0,testRecord)

    }

    @Test
    @Throws(Exception::class)
    fun given_days_step_record_successfully_deleted_When_delete_is_received_Then_record_should_not_exist_in_database() {
        //GIVEN
        val testObserver = TestObserver<StepsRecord>()
        stepsRecordDao.save(testRecord).subscribe()

        //WHEN
        stepsRecordDao.delete(testRecord).subscribe()

        //THEN
        stepsRecordDao.getDayStepsRecord(testRecord.day).toObservable().subscribe(testObserver)
        testObserver.assertNoValues()

    }

    @Test
    @Throws(Exception::class)
    fun given_days_step_record_successfully_loaded_When_getDayStepsRecord_is_received_Then_emit_days_step_record() {
        //GIVEN
        val testObserver = TestObserver<StepsRecord>()
        stepsRecordDao.save(testRecord).subscribe()

        //WHEN
        stepsRecordDao.getDayStepsRecord(testRecord.day).toObservable().subscribe(testObserver)

        //THEN
        testObserver.assertNoErrors().assertValueAt(0,testRecord)

    }



}