package com.example.stride.ui.past_steps

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.rxjava2.cachedIn
import com.example.stride.domain.steps_record.GetStepsRecordUseCase
import com.example.stride.domain.steps_record.GetTodayRecordUseCase
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PastStepsViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val getStepsRecordUseCase: GetStepsRecordUseCase,
    private val getTodayRecordUseCase: GetTodayRecordUseCase
): BaseViewModel<PastStepsAction,PastStepsState>() {

    private val reducer: Reducer<PastStepsState, PastStepsChange> = { state, change ->
        Timber.d("Received change: $change")
        when(change){
            is PastStepsChange.PastRecordsLoading -> {
                state.copy(isIdle = false, isLoading = true, isError = false, error = "")
            }
            is PastStepsChange.PastRecords -> {
                state.copy(isLoading = false, pastStepsRecords = change.records)
            }
            is PastStepsChange.PastRecordsError -> {
                state.copy(isLoading = false, isError = true, error = change.errorMessage)
            }
            is PastStepsChange.TodaysRecordLoading -> {
                state.copy(isIdle = false, isLoading = true, isError = false, error = "")
            }
            is PastStepsChange.TodaysRecord -> {
                state.copy(isLoading = false, todaysRecord = change.record)
            }
            is PastStepsChange.TodaysRecordError -> {
                state.copy(isLoading = false, isError = true, error = change.errorMessage)
            }
        }
    }

    override val initialState: PastStepsState
        get() = savedState.get<PastStepsState>(SAVED_STATE_KEY) ?: PastStepsState(isIdle = true)

    init {
        bindActions()
    }

    private fun bindActions(){

        val pastRecordsChange = actions.ofType<PastStepsAction.GetStepsRecords>()
            .switchMap {action->
                getStepsRecordUseCase.getRecords(action.filter)
                    .cachedIn(viewModelScope)
                    .subscribeOn(Schedulers.io())
                    .map<PastStepsChange> {
                        PastStepsChange.PastRecords(it)
                    }
                    .onErrorReturn {
                        PastStepsChange.PastRecordsError(it.localizedMessage ?: it.message ?:"Unknown Error")
                    }
                    .startWith(PastStepsChange.PastRecordsLoading)
            }

        val todaysRecordChange = actions.ofType<PastStepsAction.GetTodaysStepsRecord>()
            .switchMap {_->
                getTodayRecordUseCase.getTodaysRecord()
                    .subscribeOn(Schedulers.io())
                    .map<PastStepsChange> {
                        PastStepsChange.TodaysRecord(it.value)
                    }
                    .onErrorReturn {
                        PastStepsChange.TodaysRecordError(it.localizedMessage ?: it.message ?:"Unknown Error")
                    }
                    .startWith(PastStepsChange.TodaysRecordLoading)
            }

        val allChanges = Observable.merge(todaysRecordChange, pastRecordsChange)

        disposables += allChanges
            .scan(initialState, reducer)
            .filter { !it.isIdle && !it.isLoading }
            .distinctUntilChanged()
            .doOnNext {
                Timber.d("Received state: $it")
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Received subscribed state: $it")
                state.value = it
                savedState.set(SAVED_STATE_KEY,it)
            }, Timber::e)
    }

    override fun onCleared() {
        super.onCleared()
        getStepsRecordUseCase.dispose()
    }

    companion object{
        private const val SAVED_STATE_KEY = "state"
    }

}