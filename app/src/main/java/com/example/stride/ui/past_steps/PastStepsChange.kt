package com.example.stride.ui.past_steps

import androidx.paging.PagingData
import com.example.stride.data.local.entity.StepsRecord


sealed class PastStepsChange {
    object PastRecordsLoading: PastStepsChange()
    data class PastRecordsError(val errorMessage: String): PastStepsChange()
    data class PastRecords(val records:PagingData<StepsRecord>): PastStepsChange()

    object TodaysRecordLoading: PastStepsChange()
    data class TodaysRecordError(val errorMessage: String): PastStepsChange()
    data class TodaysRecord(val record:StepsRecord?): PastStepsChange()

}