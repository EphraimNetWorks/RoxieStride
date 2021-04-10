package com.example.stride.ui.past_steps

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.stride.R
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.databinding.ActivityPastStepsBinding
import com.example.stride.utils.hide
import com.example.stride.utils.show
import com.example.stride.utils.toast
import com.jaredrummler.materialspinner.MaterialSpinner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.stride.utils.setTitleColor

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
        setContentView(binding.root)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.design_default_color_primary)))
        supportActionBar?.setTitleColor(Color.WHITE)
        viewModel.observableState.observe(this, this::renderState)

        binding.pastItemsRecycler.adapter = adapter

        if (savedInstanceState == null){
            viewModel.dispatch(PastStepsAction.GetStepsRecords(Filter.PAST_YEAR))

            viewModel.dispatch(PastStepsAction.GetTodaysStepsRecord)
        }

        binding.filterSpinner.setItems(mutableListOf(
            getString(R.string.past_3_days),
            getString(R.string.past_month) ,
            getString(R.string.past_year)
        ))
        binding.filterSpinner.selectedIndex = 2

        binding.filterSpinner.setOnItemSelectedListener(object : MaterialSpinner.OnItemSelectedListener<String>{
            override fun onItemSelected(
                view: MaterialSpinner?,
                position: Int,
                id: Long,
                item: String
            ) {
                val filter = when(position){
                    0->Filter.PAST_3_DAYS
                    1->Filter.PAST_3_DAYS
                    else->Filter.PAST_YEAR
                }
                viewModel.dispatch(PastStepsAction.GetStepsRecords(filter))
            }
        })

        adapter.addLoadStateListener {
            it.decideOnState(
                showLoading = { visible ->
                    if(visible){
                        binding.progressLayout.show()
                    }else{
                        binding.progressLayout.hide()
                    }
                },
                showEmptyState = { visible ->
                    if(visible){
                        binding.pastItemsRecycler.hide()
                    }else{
                        binding.pastItemsRecycler.show()
                    }
                },
                showError = { message ->
                    toast(message)
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.light_dark_theme_menu,menu)
        val themeSwitch = (menu[0].actionView as SwitchCompat)
        themeSwitch.isChecked =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        themeSwitch.setOnCheckedChangeListener { _, _ ->
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Configuration.UI_MODE_NIGHT_NO ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        return true
    }

    private fun renderState(state: PastStepsState){
        with(state){
            when{
                isLoading-> renderPastStepsRecordsLoading()
                isError-> renderPastStepsRecordsError(error)
                todaysRecord != null || pastStepsRecords != null-> {
                    pastStepsRecords?.let {
                        renderPastStepsRecordsSuccess(it)
                    }
                    todaysRecord?.let {
                        renderTodaysRecordSuccess(it)
                    }
                }
                else->{}
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
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

    private fun renderTodaysRecordSuccess(record: StepsRecord){
        binding.progressLayout.visibility = View.GONE

        val countText = if(record.stepCount == 1){
            getString(R.string.step_count, record.stepCount)
        }else{
            getString(R.string.steps_count, record.stepCount)
        }

        binding.todaysRecordStepsCountTextview.text = countText
    }

    private inline fun CombinedLoadStates.decideOnState(
        showLoading: (Boolean) -> Unit,
        showEmptyState: (Boolean) -> Unit,
        showError: (String) -> Unit
    ) {
        showLoading(refresh is LoadState.Loading)

        showEmptyState(
            source.append is LoadState.NotLoading
                    && source.append.endOfPaginationReached
                    && adapter.itemCount == 0
        )

        val errorState = source.append as? LoadState.Error
            ?: source.prepend as? LoadState.Error
            ?: source.refresh as? LoadState.Error
            ?: append as? LoadState.Error
            ?: prepend as? LoadState.Error
            ?: refresh as? LoadState.Error

        errorState?.let { showError(it.error.toString()) }
    }

    companion object{
        fun newInstance(context:Context) = Intent(context,PastStepsActivity::class.java)
    }

}
