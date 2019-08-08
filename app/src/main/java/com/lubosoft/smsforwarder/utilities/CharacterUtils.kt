package com.lubosoft.smsforwarder.utilities

import android.text.TextUtils
import com.lubosoft.smsforwarder.data.room.PhoneNumberData

object CharacterUtils {

    private fun firstDigitOrFirstCharacter(text: String): Char {
        if (!TextUtils.isEmpty(text)) {
            return text.firstOrNull { Character.isDigit(it) } ?: text[0]
        } else {
            return ' '
        }
    }

    fun firstNonBlankLetter(item: PhoneNumberData): String {
        return if (!TextUtils.isEmpty(item.contactName)) Character.toString(item.contactName.trim()[0])
            else Character.toString(firstDigitOrFirstCharacter(item.phoneNumber.trim()))
    }

}