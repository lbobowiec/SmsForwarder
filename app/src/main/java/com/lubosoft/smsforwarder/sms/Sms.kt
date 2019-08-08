package com.lubosoft.smsforwarder.sms

data class Sms(val fromPhoneNumber: String, val textMessage: String, var fromContact: String = "") {

    fun contentWithContactName() = "$fromContact: $textMessage"

    fun contentWithPhoneNumber() = "<$fromPhoneNumber>: $textMessage"

    fun fullContent() = "$fromContact <$fromPhoneNumber>: $textMessage"

}