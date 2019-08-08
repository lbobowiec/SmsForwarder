package com.lubosoft.smsforwarder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lubosoft.smsforwarder.data.repository.TimeOffRuleRepository

class TimeOffRuleViewModelFactory(private val repository: TimeOffRuleRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = TimeOffRuleViewModel(repository) as T
}