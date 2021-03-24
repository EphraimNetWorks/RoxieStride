package com.example.stride.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stride.data.local.dao.StepsRecordDao
import com.example.stride.data.local.entity.StepsRecord

@Database(entities = [StepsRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepRecordDao(): StepsRecordDao

    companion object{

        private const val DATABASE_NAME = "Stride_DB"
        fun newInstance(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }
}