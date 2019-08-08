package com.lubosoft.smsforwarder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class BlackListPhoneNumberFragment : PhoneNumberFragment() {

    private val args: BlackListPhoneNumberFragmentArgs by navArgs()

    init {
        arguments?.putLong(PHONE_NUMBER_ID, args.phoneNumberId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding.isBlackList = true
        return view
    }

    override fun backToPhoneNumberListFragment() {
        super.backToPhoneNumberListFragment()
        findNavController().navigate(R.id.blacklist_phone_number_list_fragment)
    }
}