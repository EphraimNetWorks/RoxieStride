package com.example.stride.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.stride.R
import com.example.stride.ui.past_steps.PastStepsActivity
import com.example.stride.ui.record_step.RecordStepsActivity
import com.example.stride.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()

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
                else->renderHasTodaysRecordSuccess(isMostRecentRecordToday)
            }
        }
    }

    private fun renderHasTodaysRecordError(errorMsg: String){
        lifecycleScope.launch {
            delay(3000)
            toast(errorMsg)
            startActivity(RecordStepsActivity.newInstance(this@SplashScreenActivity))
        }
    }

    private fun renderHasTodaysRecordLoading(){
        Timber.i("Checking for tody's record")
    }

    private fun renderHasTodaysRecordSuccess(isMostRecentToday: Boolean){
        lifecycleScope.launch {
            delay(3000)
            val intent = if(isMostRecentToday){
                PastStepsActivity.newInstance(this@SplashScreenActivity)
            }else{
                RecordStepsActivity.newInstance(this@SplashScreenActivity)
            }
            startActivity(intent)
        }
    }

}