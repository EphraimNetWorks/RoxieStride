package com.example.stride.ui.splash

import com.example.stride.data.local.entity.StepsRecord

sealed class SplashChange {
    object Loading: SplashChange()
    data class HasStepsRecordForToday(val hasTodaysRecord: Boolean) : SplashChange()
    data class HasStepsRecordForTodayError(val errorMessage: String): SplashChange()
}