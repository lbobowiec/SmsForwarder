package com.lubosoft.smsforwarder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.lubosoft.smsforwarder.*
import com.lubosoft.smsforwarder.data.room.Converters
import com.lubosoft.smsforwarder.data.room.TimeOffRule
import com.lubosoft.smsforwarder.databinding.ListItemTimeoffRuleBinding
import com.lubosoft.smsforwarder.utilities.DateUtils

class TimeOffRuleListAdapter(private val onDeleteListener: TimeOffRuleViewHolder.OnDeleteButtonClickListener)
                            : RecyclerView.Adapter<TimeOffRuleListAdapter.TimeOffRuleViewHolder>() {

    private lateinit var binding: ListItemTimeoffRuleBinding

    private var timeOffRules = emptyList<TimeOffRule>() // cached copy of time off rules

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeOffRuleViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_timeoff_rule, parent, false)
        return TimeOffRuleViewHolder(binding)
    }

    private fun createOnClickListener(timeOffRuleId: Long): View.OnClickListener {
        return View.OnClickListener {
            val directions = TimeOffRuleListFragmentDirections.navigateToTimeoffRule(timeOffRuleId)
            it.findNavController().navigate(directions)
        }
    }

    override fun getItemCount() = timeOffRules.size

    override fun onBindViewHolder(holder: TimeOffRuleViewHolder, position: Int) {
        val timeOffRule = timeOffRules[position]
        holder.apply {
            val text = formatTimeOffRuleMessage(holder.itemView.context, timeOffRule)
            bind(createOnClickListener(timeOffRule.id), timeOffRule, text, onDeleteListener)
            itemView.tag = timeOffRule
        }
    }

    fun getItemAtPosition(position: Int) = timeOffRules[position]

    fun indexOfItem(element: TimeOffRule) = timeOffRules.indexOf(element)

    internal fun setTimeOffRules(timeOffRules: List<TimeOffRule>) {
        this.timeOffRules = timeOffRules
        notifyDataSetChanged()
    }

    private fun formatTimeOffRuleMessage(context: Context, timeOffRule: TimeOffRule): String {
        var messages = arrayOf(
            context.getString(R.string.dont_forwad_on_text),
            DateUtils.daysOfWeekToString(context, timeOffRule.daysOfWeek),
            context.getString(R.string.between_text),
            Converters.fromLocalTime(DateUtils.adjustTimeToDefaultLocale(context, timeOffRule.startTime)),
            context.getString(R.string.and_text),
            Converters.fromLocalTime(DateUtils.adjustTimeToDefaultLocale(context, timeOffRule.endTime))
        )
        if (DateUtils.isLessOrEqual(timeOffRule.endTime, timeOffRule.startTime)) {
            messages += context.getString(R.string.next_day_text)
        }
        return messages.joinToString(separator = " ")
    }

    class TimeOffRuleViewHolder(private val binding: ListItemTimeoffRuleBinding): RecyclerView.ViewHolder(binding.root) {

        interface OnDeleteButtonClickListener {
            fun onDeleteClick(item: TimeOffRule)
        }

        fun bind(listener: View.OnClickListener, item: TimeOffRule, text: String, deleteListener: OnDeleteButtonClickListener) {
            binding.apply {
                clickListener = listener
                timeOffRule = item
                timeOffRuleTextView.text = text
                onDeleteListener = deleteListener
                executePendingBindings()
            }
        }
    }
}