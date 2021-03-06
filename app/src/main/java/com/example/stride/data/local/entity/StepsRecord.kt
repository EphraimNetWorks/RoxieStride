package com.example.stride.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "steps_records")
data class StepsRecord (
    @PrimaryKey @SerializedName("day") @Expose val day:String,
    @SerializedName("step_count") @Expose val stepCount: Int
) : Parcelable