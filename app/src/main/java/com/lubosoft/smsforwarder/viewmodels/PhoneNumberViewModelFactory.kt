package com.lubosoft.smsforwarder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lubosoft.smsforwarder.data.repository.PhoneNumberRepository

class PhoneNumberViewModelFactory(private val repository: PhoneNumberRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = PhoneNumberViewModel(repository) as T
}