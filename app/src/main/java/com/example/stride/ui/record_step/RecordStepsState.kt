package com.example.stride.ui.record_step

import com.example.stride.data.local.entity.StepsRecord
import com.ww.roxie.BaseState

data class RecordStepsState(
    val isIdle: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val error: String = "",
): BaseState