package com.lubosoft.smsforwarder

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController

class TargetPhoneNumberListFragment : PhoneNumberListFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFloatingActionButton()
    }

    private fun initFloatingActionButton() {
        binding.fab.setOnClickListener {
            val directions = TargetPhoneNumberListFragmentDirections.navigateToTargetPhoneNumber(-1L)
            findNavController().navigate(directions)
        }
    }
}