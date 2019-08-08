package com.lubosoft.smsforwarder.sms

import android.telephony.SmsManager
import android.util.Log

class SmsSender() {

    companion object {
        private const val TAG = "SmsSender"
    }

    fun composeMessage(phoneNumbers: List<String>, message: String) {
        for (phoneNumber in phoneNumbers) {
            composeMessage(phoneNumber, message)
        }
    }

    private fun composeMessage(destinationPhoneNumber: String, message: String) {
        Log.d(TAG, "Trying to send SMS to $destinationPhoneNumber")
        val smsManager = SmsManager.getDefault()
        val messageList = smsManager.divideMessage(message)
        if (messageList.size > 1) {
            smsManager.sendMultipartTextMessage(destinationPhoneNumber, null, messageList, null, null)
        } else {
            smsManager.sendTextMessage(destinationPhoneNumber, null, message, null, null)
        }
        Log.d(TAG, "SMS has been sent to $destinationPhoneNumber")
    }

}