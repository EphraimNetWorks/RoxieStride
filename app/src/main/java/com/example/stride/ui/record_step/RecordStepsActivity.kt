package com.example.stride.ui.record_step

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
import com.example.stride.R
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.databinding.ActivityRecordStepBinding
import com.example.stride.ui.past_steps.PastStepsActivity
import com.example.stride.utils.dismissKeyboard
import com.example.stride.utils.setTitleColor
import com.example.stride.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecordStepsActivity : AppCompatActivity() {

    private val viewModel:RecordStepsViewModel by viewModels()

    private val binding by lazy {
        ActivityRecordStepBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.design_default_color_primary)))
        supportActionBar?.setTitleColor(Color.WHITE)

        viewModel.observableState.observe(this, this::renderState)

        binding.saveButton.setOnClickListener {
            val stepCount = binding.stepCountEdittext.text.toString().toIntOrNull()
            if (stepCount != null){
                dismissKeyboard()
                val newRecord = StepsRecord(DateTimeFormatter.ISO_DATE.format(LocalDate.now()), stepCount)
                viewModel.dispatch(RecordStepsAction.AddNewStepsRecord(newRecord))
            }else{
                toast(getString(R.string.new_steps_record_validation_error_msg))
            }
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

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun renderState(state: RecordStepsState){
        with(state){
            when{
                isLoading-> renderNewRecordLoading()
                isError-> renderNewRecordError(error)
                isSuccess->renderNewRecordSuccess()
            }
        }
    }

    private fun renderNewRecordError(errorMsg: String){
        binding.progressLayout.visibility = View.GONE
        toast(errorMsg)
    }

    private fun renderNewRecordLoading(){
        binding.progressLayout.visibility = View.VISIBLE
    }

    private fun renderNewRecordSuccess(){
        binding.progressLayout.visibility = View.GONE
        startActivity(PastStepsActivity.newInstance(this))
    }

    companion object{
        fun newInstance(context: Context) = Intent(context, RecordStepsActivity::class.java)
    }
}