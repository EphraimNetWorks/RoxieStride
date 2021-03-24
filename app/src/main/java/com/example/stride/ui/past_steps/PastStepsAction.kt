package com.example.stride.ui.past_steps

import com.ww.roxie.BaseAction

sealed class PastStepsAction: BaseAction {

    data class GetStepsRecords(val filter: Filter): PastStepsAction()

    object GetTodaysStepsRecord: PastStepsAction()

}