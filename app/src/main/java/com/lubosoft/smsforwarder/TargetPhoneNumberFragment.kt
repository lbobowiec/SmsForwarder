package com.lubosoft.smsforwarder

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class TargetPhoneNumberFragment : PhoneNumberFragment() {

    private val args: TargetPhoneNumberFragmentArgs by navArgs()

    init {
        arguments?.putLong(PHONE_NUMBER_ID, args.phoneNumberId)
    }

    override fun backToPhoneNumberListFragment() {
        super.backToPhoneNumberListFragment()
        findNavController().navigate(R.id.target_phone_number_list_fragment)
    }
}