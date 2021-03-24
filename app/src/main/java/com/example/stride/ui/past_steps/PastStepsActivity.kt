package com.example.stride.ui.past_steps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.paging.PagingData
import com.example.stride.R
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.databinding.ActivityPastStepsBinding
import com.example.stride.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PastStepsActivity : AppCompatActivity() {

    private val viewModel: PastStepsViewModel by viewModels()

    private val adapter = PastRecordsAdapter()

    private val binding by lazy {
        ActivityPastStepsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_steps)

        viewModel.observableState.observe(this, this::renderState)

        binding.pastItemsRecycler.adapter = adapter

        viewModel.dispatch(PastStepsAction.GetStepsRecords(Filter.PAST_YEAR))

        viewModel.dispatch(PastStepsAction.GetTodaysStepsRecord)

    }



    private fun renderState(state: PastStepsState){
        with(state){
            when{
                isLoading-> renderPastStepsRecordsLoading()
                isError-> renderPastStepsRecordsError(error)
                pastStepsRecords != null->renderPastStepsRecordsSuccess(pastStepsRecords)
            }
        }
    }

    private fun renderPastStepsRecordsError(errorMsg: String){
        binding.progressLayout.visibility = View.GONE
        toast(errorMsg)
    }

    private fun renderPastStepsRecordsLoading(){
        binding.progressLayout.visibility = View.VISIBLE
    }

    private fun renderPastStepsRecordsSuccess(records: PagingData<StepsRecord>){
        binding.progressLayout.visibility = View.GONE
        adapter.submitData(lifecycle, records)
    }

    companion object{
        fun newInstance(context:Context) = Intent(context,PastStepsActivity::class.java)
    }

}