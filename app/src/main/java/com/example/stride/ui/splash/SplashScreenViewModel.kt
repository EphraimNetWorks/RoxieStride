package com.example.stride.ui.splash

import androidx.lifecycle.SavedStateHandle
import com.example.stride.domain.steps_record.GetTodayRecordUseCase
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val getTodayRecordUseCase: GetTodayRecordUseCase
) : BaseViewModel<SplashAction, SplashState>() {

    private val reducer: Reducer<SplashState, SplashChange> = { state, change ->
        when(change){
            is SplashChange.Loading-> state.copy(isIdle = false, isLoading = true, isError = false, error = "")
            is SplashChange.HasStepsRecordForToday -> {
                val isRecentToday = change.hasTodaysRecord
                state.copy(isIdle = false, isLoading = false, isMostRecentRecordToday = isRecentToday)
            }
            is SplashChange.HasStepsRecordForTodayError -> state.copy(
                isIdle = false,
                isLoading = false,
                isError = true,
                error = change.errorMessage
            )
        }
    }

    override val initialState: SplashState
        get() = savedState.get<SplashState>(SAVED_STATE_KEY) ?: SplashState(isIdle = true)

    init {
        bindActions()
    }

    private fun bindActions(){
        disposables += actions.ofType<SplashAction.CheckForTodaysStepRecord>()
            .switchMap {_->
                getTodayRecordUseCase.getTodaysRecord()
                    .subscribeOn(Schedulers.io())
                    .map<SplashChange> {
                        SplashChange.HasStepsRecordForToday(it.value != null)
                    }
                    .onErrorReturn {
                        SplashChange.HasStepsRecordForTodayError(it.localizedMessage ?: it.message ?:"Unknown Error")
                    }
                    .startWith(SplashChange.Loading)
            }
            .subscribeOn(Schedulers.io())
            .scan(initialState, reducer)
            .filter { !it.isIdle && !it.isLoading }
            .distinctUntilChanged()
            .subscribe({
                state.postValue(it)
                savedState.set(SAVED_STATE_KEY, it)
            }, Timber::e)

        dispatch(SplashAction.CheckForTodaysStepRecord)


    }

    override fun onCleared() {
        super.onCleared()
        getTodayRecordUseCase.dispose()
    }

    companion object{
        private const val SAVED_STATE_KEY = "state"
    }
}