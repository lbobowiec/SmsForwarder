package com.lubosoft.smsforwarder

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lubosoft.smsforwarder.data.room.Converters
import com.lubosoft.smsforwarder.data.room.DayOfWeek
import com.lubosoft.smsforwarder.data.room.LocalTime
import com.lubosoft.smsforwarder.data.room.TimeOffRule
import com.lubosoft.smsforwarder.databinding.FragmentTimeoffRuleBinding
import com.lubosoft.smsforwarder.utilities.DateUtils
import com.lubosoft.smsforwarder.utilities.InjectorUtils
import com.lubosoft.smsforwarder.viewmodels.TimeOffRuleViewModel

class TimeOffRuleFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    companion object {
        private const val TIME_PICKER = "timePicker"
    }

    private val args: TimeOffRuleFragmentArgs by navArgs()
    private lateinit var binding: FragmentTimeoffRuleBinding
    private lateinit var viewModel: TimeOffRuleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeoff_rule, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = InjectorUtils.provideTimeOffRuleViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, factory).get(TimeOffRuleViewModel::class.java)
        if (args.timeoffRuleId == -1L) {
            initSaveButton(null)
            initLaunchTimePickerButton(binding.startTimeLaunchButton, binding.startTimeValueTextView)
            initLaunchTimePickerButton(binding.endTimeLaunchButton, binding.endTimeValueTextView)
        } else {
            viewModel.getTimeOffRule(args.timeoffRuleId).observe(this, Observer<TimeOffRule> {
                initView(it)
                initSaveButton(it)
                initLaunchTimePickerButton(binding.startTimeLaunchButton, binding.startTimeValueTextView, it.startTime.hour, it.startTime.minute)
                initLaunchTimePickerButton(binding.endTimeLaunchButton, binding.endTimeValueTextView, it.endTime.hour, it.endTime.minute)
            })
        }
        initSwitchButtons()
        initCancelButton()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        binding.saveTimeoffRuleButton.isEnabled = isAnyDayOfWeekSelected()
    }

    private fun initView(item: TimeOffRule) {
        val daysOfWeekMutableList = item.daysOfWeek
        if (daysOfWeekMutableList.isNotEmpty()) {
            for (day in daysOfWeekMutableList) {
                when (day) {
                    DayOfWeek.MONDAY -> binding.mondayDayOfWeek.isChecked = true
                    DayOfWeek.TUESDAY -> binding.tuesdayDayOfWeek.isChecked = true
                    DayOfWeek.WEDNESDAY -> binding.wednesdayDayOfWeek.isChecked = true
                    DayOfWeek.THURSDAY -> binding.thursdayDayOfWeek.isChecked = true
                    DayOfWeek.FRIDAY -> binding.fridayDayOfWeek.isChecked = true
                    DayOfWeek.SATURDAY -> binding.saturdayDayOfWeek.isChecked = true
                    DayOfWeek.SUNDAY -> binding.sundayDayOfWeek.isChecked = true
                }
            }
        }
        binding.startTimeValueTextView.text = Converters.fromLocalTime(DateUtils.adjustTimeToDefaultLocale(requireContext(), item.startTime))
        binding.endTimeValueTextView.text = Converters.fromLocalTime(DateUtils.adjustTimeToDefaultLocale(requireContext(), item.endTime))
    }



    private fun initSwitchButtons() {
        binding.apply {
            mondayDayOfWeek.setOnCheckedChangeListener(this@TimeOffRuleFragment)
            tuesdayDayOfWeek.setOnCheckedChangeListener(this@TimeOffRuleFragment)
            wednesdayDayOfWeek.setOnCheckedChangeListener(this@TimeOffRuleFragment)
            thursdayDayOfWeek.setOnCheckedChangeListener(this@TimeOffRuleFragment)
            fridayDayOfWeek.setOnCheckedChangeListener(this@TimeOffRuleFragment)
            saturdayDayOfWeek.setOnCheckedChangeListener(this@TimeOffRuleFragment)
            sundayDayOfWeek.setOnCheckedChangeListener(this@TimeOffRuleFragment)
        }
    }

    private fun initLaunchTimePickerButton(button: ImageButton, textView: TextView, hour: Int = 12, min: Int = 0) {
        button.setOnClickListener {
            val timePicker = TimePickerFragment.newInstance(hour, min, DateFormat.is24HourFormat(requireContext()))
            timePicker.setListener(
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    textView.text = buildTimeValue(hourOfDay, minute) })
            timePicker.show(fragmentManager!!, TIME_PICKER)
        }
    }

    private fun buildTimeValue(hourOfDay: Int, minute: Int): String {
        val v = if (DateFormat.is24HourFormat(requireContext())) {
            LocalTime(hourOfDay, minute)
        } else {
            var amPm = "AM"
            var selectedHour = hourOfDay
            if (hourOfDay > 11) {
                selectedHour -= 12
                amPm = "PM"
            }
            LocalTime(selectedHour, minute, amPm)
        }
        return Converters.fromLocalTime(v)
    }

    private fun buildDayOfWeeks(): MutableList<DayOfWeek> {
        val daysOfWeek = mutableListOf<DayOfWeek>()
        binding.apply {
            if (mondayDayOfWeek.isChecked) {
                daysOfWeek.add(DayOfWeek.MONDAY)
            }
            if (tuesdayDayOfWeek.isChecked) {
                daysOfWeek.add(DayOfWeek.TUESDAY)
            }
            if (wednesdayDayOfWeek.isChecked) {
                daysOfWeek.add(DayOfWeek.WEDNESDAY)
            }
            if (thursdayDayOfWeek.isChecked) {
                daysOfWeek.add(DayOfWeek.THURSDAY)
            }
            if (fridayDayOfWeek.isChecked) {
                daysOfWeek.add(DayOfWeek.FRIDAY)
            }
            if (saturdayDayOfWeek.isChecked) {
                daysOfWeek.add(DayOfWeek.SATURDAY)
            }
            if (sundayDayOfWeek.isChecked) {
                daysOfWeek.add(DayOfWeek.SUNDAY)
            }
        }
        return daysOfWeek
    }

    private fun isAnyDayOfWeekSelected(): Boolean {
        binding.apply {
            return mondayDayOfWeek.isChecked || tuesdayDayOfWeek.isChecked || wednesdayDayOfWeek.isChecked
                    || thursdayDayOfWeek.isChecked || fridayDayOfWeek.isChecked
                    || saturdayDayOfWeek.isChecked || sundayDayOfWeek.isChecked
        }
    }

    private fun initSaveButton(item: TimeOffRule?) {
        binding.apply {
            if (item == null) {
                saveTimeoffRuleButton.isEnabled = false
            }

            saveTimeoffRuleButton.setOnClickListener {
                val daysOfWeek = buildDayOfWeeks()
                val startTime = Converters.stringToLocalTime(startTimeValueTextView.text.toString())
                val endTime = Converters.stringToLocalTime(endTimeValueTextView.text.toString())
                if (item != null) {
                    item.daysOfWeek = daysOfWeek
                    item.startTime = startTime
                    item.endTime = endTime
                    viewModel.update(item)
                } else {
                    viewModel.insert(TimeOffRule(daysOfWeek = daysOfWeek, startTime = startTime, endTime = endTime))
                }
                backToTimeOffRuleListFragment()
            }
        }
    }

    private fun initCancelButton() {
        binding.cancelTimeoffRuleButton.setOnClickListener {
            backToTimeOffRuleListFragment()
        }
    }

    private fun backToTimeOffRuleListFragment() {
        findNavController().navigate(R.id.timeoff_rule_list_fragment)
    }

}