package com.lubosoft.smsforwarder.sms

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import androidx.preference.PreferenceManager
import com.lubosoft.smsforwarder.contacts.ContactsResolver
import com.lubosoft.smsforwarder.data.room.AppDatabase
import com.lubosoft.smsforwarder.utilities.DateUtils
import java.util.*

object SmsBus {

    private const val TAG = "SmsBus"
    private const val APPEND_CONTACT_NAME_PREFERENCE_KEY = "append_contact_name_settings"
    private const val APPEND_PHONE_NUMBER_PREFERENCE_KEY = "append_phone_number_settings"

    fun postSms(context: Context, sms: Sms) {
        val phoneNumberDao = AppDatabase.getInstance(context).phoneNumberDao()
        val blacklistNumbers = phoneNumberDao.getPhoneNumbers(isBlackList = true)
        if (sms.fromPhoneNumber in blacklistNumbers) {
            Log.d(TAG, "${sms.fromPhoneNumber} is in the blacklist numbers")
            return
        }

        val timeOffRuleDao = AppDatabase.getInstance(context).timeOffRuleDao()
        val timeOffRules = timeOffRuleDao.getAllTimeOffRules()
        if (timeOffRules.isNotEmpty()) {
            val now = Date()
            if (DateUtils.isDateWithinTimeIntervals(now, timeOffRules, DateFormat.is24HourFormat(context))) {
                Log.d(TAG, "Current date: $now falls in time-off rules")
                return
            }
        }

        val allPhoneNumbers: List<String> = phoneNumberDao.getPhoneNumbers(isBlackList = false)
        val sender = SmsSender()
        val message = prepareMessage(context, sms)
        sender.composeMessage(allPhoneNumbers, message)
    }

    private fun prepareMessage(context: Context, sms: Sms): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val appendContactNameSettings = sharedPreferences.getBoolean(SmsBus.APPEND_CONTACT_NAME_PREFERENCE_KEY, true)
        val appendOriginalSenderPhoneNumber = sharedPreferences.getBoolean(SmsBus.APPEND_PHONE_NUMBER_PREFERENCE_KEY, true)

        return when {
            appendContactNameSettings && appendOriginalSenderPhoneNumber -> {
                sms.fromContact = ContactsResolver(context.contentResolver).retrieveContactNameByPhoneNumber(sms.fromPhoneNumber)
                if (sms.fromContact.isNotEmpty()) sms.fullContent() else sms.contentWithPhoneNumber()
            }
            appendContactNameSettings -> {
                sms.fromContact = ContactsResolver(context.contentResolver).retrieveContactNameByPhoneNumber(sms.fromPhoneNumber)
                if(sms.fromContact.isNotEmpty()) sms.contentWithContactName() else sms.textMessage
            }
            appendOriginalSenderPhoneNumber -> { sms.contentWithPhoneNumber() }
            else -> { sms.textMessage }
        }
    }

}