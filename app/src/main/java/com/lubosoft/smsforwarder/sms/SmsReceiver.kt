package com.lubosoft.smsforwarder.sms

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SmsReceiver"
        private const val PDU_TYPE = "pdus"
        private const val AUTO_FORWARD_SMS_KEY = "auto_forward_sms_settings"
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Intent received ${intent.action}")
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION && autoForwardEnabled(context)) {
            val smsMessages = getMessagesFromIntent(intent)
            val smsSender = getSmsSender(smsMessages[0])
            val smsTextMessage = getSmsTextMessage(smsMessages)
            Log.d(TAG, "Received SMS from $smsSender $smsTextMessage")
            val sms = Sms(fromPhoneNumber = smsSender, textMessage = smsTextMessage)
            GlobalScope.launch(Dispatchers.IO) {
                SmsBus.postSms(context, sms)
            }
        }
    }

    private fun autoForwardEnabled(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(AUTO_FORWARD_SMS_KEY, true)
    }

    private fun getSmsSender(smsMessage: SmsMessage?): String {
        return smsMessage?.displayOriginatingAddress ?: ""
    }

    private fun getSmsTextMessage(smsMessages: Array<SmsMessage?>): String {
        val smsBody = StringBuilder()
        for (smsMessage in smsMessages) {
            smsBody.append(smsMessage?.messageBody)
        }
        return smsBody.toString()
    }

    private fun getMessagesFromIntent(intent: Intent): Array<SmsMessage?> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) getMessagesFromIntentApi19(intent)
            else getMessagesFromIntentApi15(intent)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getMessagesFromIntentApi19(intent: Intent): Array<SmsMessage?> {
        return Telephony.Sms.Intents.getMessagesFromIntent(intent)
    }

    @Suppress("DEPRECATION")
    private fun getMessagesFromIntentApi15(intent: Intent): Array<SmsMessage?> {
        val pdus = intent.getSerializableExtra(PDU_TYPE) as Array<*>
        val smsMessages = arrayOfNulls<SmsMessage>(pdus.size)
        for (index in pdus.indices) {
            smsMessages[index] = SmsMessage.createFromPdu(pdus[index] as ByteArray)
        }
        return smsMessages
    }
}
