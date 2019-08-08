package com.lubosoft.smsforwarder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class BlackListPhoneNumberListFragment : PhoneNumberListFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding.isBlackList = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFloatingActionButton()
    }

    private fun initFloatingActionButton() {
        binding.fab.setOnClickListener {
            val directions = BlackListPhoneNumberListFragmentDirections.navigateToBlacklistPhoneNumber(-1L)
            findNavController().navigate(directions)
        }
    }
}