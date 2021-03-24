package com.example.stride.ui.record_step



sealed class RecordStepsChange {
    object NewRecordLoading: RecordStepsChange()
    data class NewRecordError(val errorMessage: String): RecordStepsChange()
    object NewRecordSuccess: RecordStepsChange()
}