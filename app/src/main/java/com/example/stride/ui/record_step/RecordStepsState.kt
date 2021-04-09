package com.example.stride.ui.record_step

import android.os.Parcelable
import com.ww.roxie.BaseState
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordStepsState(
    val isIdle: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val error: String = "",
): BaseState, Parcelable