package com.example.stride.ui.record_step

import androidx.lifecycle.SavedStateHandle
import com.example.stride.domain.steps_record.SaveStepsRecordUseCase
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecordStepsViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val saveStepsRecordUseCase: SaveStepsRecordUseCase
): BaseViewModel<RecordStepsAction,RecordStepsState>() {

    private val reducer: Reducer<RecordStepsState, RecordStepsChange> = { state, change ->
        val newState = when(change){
            is RecordStepsChange.NewRecordLoading-> state.copy(isIdle = false, isLoading = true, isError = false, error = "")
            is RecordStepsChange.NewRecordSuccess -> state.copy(isLoading = false, isSuccess = true)
            is RecordStepsChange.NewRecordError -> state.copy(isLoading = false, isError = true, error = change.errorMessage)
        }
        savedState.set(SAVED_STATE_KEY,newState)
        newState
    }

    override val initialState: RecordStepsState
        get() = savedState.get<RecordStepsState>(SAVED_STATE_KEY) ?: RecordStepsState(isIdle = true)

    init {
        bindActions()
    }

    private fun bindActions(){

        val newRecordChange = actions.ofType<RecordStepsAction.AddNewStepsRecord>()
            .switchMap {action->
                saveStepsRecordUseCase.saveRecord(action.newRecord)
                    .subscribeOn(Schedulers.io())
                    .map<RecordStepsChange> {
                        RecordStepsChange.NewRecordSuccess
                    }
                    .onErrorReturn {
                        RecordStepsChange.NewRecordError(it.localizedMessage ?: it.message ?:"Unknown Error")
                    }
                    .startWith(RecordStepsChange.NewRecordLoading)
            }

        disposables += newRecordChange
            .scan(initialState, reducer)
            .filter { !it.isIdle && !it.isLoading }
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }

    override fun onCleared() {
        super.onCleared()
        saveStepsRecordUseCase.dispose()
    }

    companion object{
        private const val SAVED_STATE_KEY = "state"
    }

}