package com.lubosoft.smsforwarder.utilities

import android.content.Context
import com.lubosoft.smsforwarder.data.repository.PhoneNumberRepository
import com.lubosoft.smsforwarder.data.repository.TimeOffRuleRepository
import com.lubosoft.smsforwarder.data.room.AppDatabase
import com.lubosoft.smsforwarder.viewmodels.PhoneNumberViewModelFactory
import com.lubosoft.smsforwarder.viewmodels.TimeOffRuleViewModelFactory

object InjectorUtils {

    private fun getPhoneNumberRepository(context: Context): PhoneNumberRepository {
        return PhoneNumberRepository.getInstance(
            AppDatabase.getInstance(context).phoneNumberDao()
        )
    }

    private fun getTimeOffRuleRepository(context: Context): TimeOffRuleRepository {
        return TimeOffRuleRepository.getInstance(
            AppDatabase.getInstance(context).timeOffRuleDao()
        )
    }

    fun providePhoneNumberViewModelFactory(context: Context): PhoneNumberViewModelFactory {
        val repository = getPhoneNumberRepository(context)
        return PhoneNumberViewModelFactory(repository)
    }

    fun provideTimeOffRuleViewModelFactory(context: Context): TimeOffRuleViewModelFactory {
        val repository = getTimeOffRuleRepository(context)
        return TimeOffRuleViewModelFactory(repository)
    }

}