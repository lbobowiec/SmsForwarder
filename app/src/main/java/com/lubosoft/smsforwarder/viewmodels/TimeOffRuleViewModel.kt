package com.lubosoft.smsforwarder.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lubosoft.smsforwarder.data.repository.TimeOffRuleRepository
import com.lubosoft.smsforwarder.data.room.TimeOffRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TimeOffRuleViewModel(private val repository: TimeOffRuleRepository) : ViewModel() {

    fun getAll() = repository.getAll()

    fun getTimeOffRule(timeOffRuleId: Long): LiveData<TimeOffRule> {
        return repository.getTimeOffRule(timeOffRuleId)
    }

    fun insert(timeOffRule: TimeOffRule) = viewModelScope.launch {
        repository.insert(timeOffRule)
    }

    fun update(timeOffRule: TimeOffRule) = viewModelScope.launch {
        repository.update(timeOffRule)
    }

    fun delete(timeOffRule: TimeOffRule) = viewModelScope.launch {
        repository.delete(timeOffRule)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}