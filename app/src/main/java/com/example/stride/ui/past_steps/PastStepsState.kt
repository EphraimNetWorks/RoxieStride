package com.example.stride.ui.past_steps

import androidx.paging.PagingData
import com.example.stride.data.local.entity.StepsRecord
import com.ww.roxie.BaseState

data class PastStepsState(
    val isIdle: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val error: String = "",
    val pastStepsRecords: PagingData<StepsRecord>? = null,
    val todaysRecord: StepsRecord? = null
): BaseState