package com.lubosoft.smsforwarder.sms

import android.content.res.Resources
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Patterns

class SmsHelper {

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return if (TextUtils.isEmpty(phoneNumber)) {
            false
        } else {
            Patterns.PHONE.matcher(phoneNumber).matches()
        }
    }

    @Suppress("DEPRECATION")
    fun formatPhoneNumber(rawPhoneNumber: String?, countryIso: String = ""): String {
        if (rawPhoneNumber == null) {
            return ""
        }

        var countryName = countryIso
        if (countryName.isBlank()) {
            countryName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales[0].country
            } else {
                Resources.getSystem().configuration.locale.country
            }
        }

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            PhoneNumberUtils.formatNumber(rawPhoneNumber)
        } else {
            PhoneNumberUtils.formatNumber(rawPhoneNumber, countryName)
        }
    }

}