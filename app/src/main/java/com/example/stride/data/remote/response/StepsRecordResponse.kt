package com.example.stride.data.remote.response

import com.example.stride.data.local.entity.StepsRecord
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StepsRecordResponse(
    @SerializedName("data") @Expose val data: List<StepsRecord>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StepsRecordResponse

        if (data != null) {
            if (other.data == null) return false
            if (!data.toTypedArray().contentEquals(other.data.toTypedArray())) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        return data?.toTypedArray()?.contentHashCode() ?: 0
    }
}