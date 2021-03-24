package com.example.stride.ui.splash

import com.ww.roxie.BaseAction

sealed class SplashAction: BaseAction {

    object CheckForTodaysStepRecord : SplashAction()

}