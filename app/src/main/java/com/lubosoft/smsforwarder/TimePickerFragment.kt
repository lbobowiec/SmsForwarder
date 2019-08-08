package com.lubosoft.smsforwarder

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class TimePickerFragment private constructor(private val hour: Int, private val minute: Int, private val is24Hours: Boolean) : DialogFragment() {

    private var listener: TimePickerDialog.OnTimeSetListener? = null

    companion object {
        fun newInstance(hour: Int, minute: Int, is24Hours: Boolean) = TimePickerFragment(hour, minute, is24Hours)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(requireContext(), listener, hour, minute, is24Hours)
    }

    fun setListener(listener: TimePickerDialog.OnTimeSetListener) {
        this.listener = listener
    }

}