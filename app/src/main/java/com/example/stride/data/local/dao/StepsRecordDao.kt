package com.example.stride.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.stride.data.local.entity.StepsRecord
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface StepsRecordDao{

    @Query("SELECT * FROM steps_records WHERE day=:day LIMIT 1")
    fun getDayStepsRecord(day:String): Flowable<StepsRecord?>

    @Query("SELECT * FROM steps_records WHERE day >= :fromDay")
    fun getStepRecords(fromDay: String):PagingSource<Int,StepsRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg record: StepsRecord): Completable

    @Delete
    fun delete(record: StepsRecord): Completable
}