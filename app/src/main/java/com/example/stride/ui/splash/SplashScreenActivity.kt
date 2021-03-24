package com.example.stride.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.stride.R
import com.example.stride.databinding.ActivitySplashScreenBinding
import com.example.stride.ui.past_steps.PastStepsActivity
import com.example.stride.ui.record_step.RecordStepsActivity
import com.example.stride.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()

    private val binding by lazy {
        ActivitySplashScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        viewModel.observableState.observe(this, this::renderState)

        viewModel.dispatch(SplashAction.CheckForTodaysStepRecord)
    }

    private fun renderState(state: SplashState){
        with(state){
            when{
                isLoading-> renderHasTodaysRecordLoading()
                isError-> renderHasTodaysRecordError(error)
                isMostRecentRecordToday->renderHasTodaysRecordSuccess(isMostRecentRecordToday)
            }
        }
    }

    private fun renderHasTodaysRecordError(errorMsg: String){
        toast(errorMsg)
        startActivity(RecordStepsActivity.newInstance(this))
    }

    private fun renderHasTodaysRecordLoading(){
        Timber.i("Checking for tody's record")
    }

    private fun renderHasTodaysRecordSuccess(isMostRecentToday: Boolean){
        val intent = if(isMostRecentToday){
            RecordStepsActivity.newInstance(this)
        }else{
            PastStepsActivity.newInstance(this)
        }
        startActivity(intent)
    }

}