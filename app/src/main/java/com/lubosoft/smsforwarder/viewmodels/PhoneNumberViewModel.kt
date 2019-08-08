package com.lubosoft.smsforwarder.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lubosoft.smsforwarder.data.repository.PhoneNumberRepository
import com.lubosoft.smsforwarder.data.room.PhoneNumberData
import kotlinx.coroutines.*

class PhoneNumberViewModel(private val repository: PhoneNumberRepository) : ViewModel() {

    fun getAllPhoneNumbers(isBlackList: Boolean = false) = repository.getAllNumbers(isBlackList)

    fun getPhoneNumber(phoneNumberId: Long): LiveData<PhoneNumberData> {
        return repository.getPhoneNumber(phoneNumberId)
    }

    fun insert(phoneNumberData: PhoneNumberData) = viewModelScope.launch {
        repository.insert(phoneNumberData)
    }

    fun update(phoneNumberData: PhoneNumberData) = viewModelScope.launch {
        repository.update(phoneNumberData)
    }

    fun delete(phoneNumberData: PhoneNumberData) = viewModelScope.launch {
        repository.delete(phoneNumberData)
    }

    fun deleteAll(isBlackList: Boolean = false) = viewModelScope.launch {
        repository.deleteAll(isBlackList)
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}