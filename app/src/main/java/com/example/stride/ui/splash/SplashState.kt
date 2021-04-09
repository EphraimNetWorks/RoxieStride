package com.example.stride.ui.splash

import android.os.Parcelable
import com.example.stride.data.local.entity.StepsRecord
import com.ww.roxie.BaseState
import kotlinx.parcelize.Parcelize

@Parcelize
data class SplashState(
    val isMostRecentRecordToday: Boolean = false,
    val isIdle: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val error: String = "",
): BaseState, Parcelable