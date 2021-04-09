package com.example.stride.ui.past_steps

import android.os.Parcel
import android.os.Parcelable
import androidx.paging.PagingData
import com.example.stride.data.local.entity.StepsRecord
import com.ww.roxie.BaseState
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class PastStepsState(
        val isIdle: Boolean = false,
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val error: String = "",
        val pastStepsRecords: PagingData<StepsRecord>? = null,
        val todaysRecord: StepsRecord? = null
): BaseState, Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readByte() != 0.toByte(),
                parcel.readByte() != 0.toByte(),
                parcel.readByte() != 0.toByte(),
                parcel.readString() ?: "",
                null,
                parcel.readParcelable(StepsRecord::class.java.classLoader)
        )

        companion object : Parceler<PastStepsState> {

                override fun PastStepsState.write(parcel: Parcel, flags: Int) {
                        parcel.writeByte(if (isIdle) 1 else 0)
                        parcel.writeByte(if (isLoading) 1 else 0)
                        parcel.writeByte(if (isError) 1 else 0)
                        parcel.writeString(error)
                        parcel.writeParcelable(todaysRecord, flags)
                }

                override fun create(parcel: Parcel): PastStepsState {
                        return PastStepsState(parcel)
                }
        }
}