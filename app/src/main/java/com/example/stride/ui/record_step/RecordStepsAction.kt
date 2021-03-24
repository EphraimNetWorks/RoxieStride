package com.example.stride.ui.record_step

import com.example.stride.data.local.entity.StepsRecord
import com.ww.roxie.BaseAction

sealed class RecordStepsAction: BaseAction {

    data class AddNewStepsRecord(val newRecord: StepsRecord) : RecordStepsAction()

}