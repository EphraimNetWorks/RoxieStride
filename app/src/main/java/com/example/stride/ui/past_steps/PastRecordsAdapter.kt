package com.example.stride.ui.past_steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stride.R
import com.example.stride.data.local.entity.StepsRecord
import com.example.stride.databinding.PastStepsRecordItemBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class PastRecordsAdapter :
    PagingDataAdapter<StepsRecord, PastRecordsAdapter.PastRecordsViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastRecordsViewHolder {
        return PastRecordsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PastRecordsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }



    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StepsRecord>(){
            override fun areItemsTheSame(oldItem: StepsRecord, newItem: StepsRecord): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: StepsRecord, newItem: StepsRecord): Boolean {
                return oldItem == newItem
            }

        }
    }

    class PastRecordsViewHolder(view: View):RecyclerView.ViewHolder(view){

        private val binding by lazy {
            PastStepsRecordItemBinding.bind(view)
        }

        fun bind(record: StepsRecord){
            val fromFormat = DateTimeFormatter.ISO_DATE
            val toFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
            binding.dateTextview.text = toFormat.format(LocalDate.parse(record.day,fromFormat))

            val countText = if(record.stepCount == 1){
                itemView.context.getString(R.string.step_count, record.stepCount)
            }else{
                itemView.context.getString(R.string.steps_count, record.stepCount)
            }
            binding.stepCountTextview.text = countText
        }

        companion object{
            fun create(parent: ViewGroup): PastRecordsViewHolder{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.past_steps_record_item, parent, false)
                return PastRecordsViewHolder(view)
            }
        }
    }
}