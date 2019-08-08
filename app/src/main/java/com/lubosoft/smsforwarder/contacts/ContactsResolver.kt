package com.lubosoft.smsforwarder.contacts

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import com.lubosoft.smsforwarder.data.room.PhoneNumberData

class ContactsResolver(private val contentResolver: ContentResolver) {

    fun retrieveContact(contactUri: Uri): PhoneNumberData {
        val phoneNumberData = PhoneNumberData(phoneNumber = "")
        val contactsContentResolver = contentResolver.query(contactUri, null, null, null, null)
        contactsContentResolver?.let {
            contactsContentResolver.moveToFirst()
            val id = contactsContentResolver.getString(contactsContentResolver.getColumnIndex(ContactsContract.Contacts._ID))
            val value = Integer.parseInt(contactsContentResolver.getString(contactsContentResolver.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))
            if (value > 0) {
                val phonesContentResolver = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?", arrayOf(id), null)
                phonesContentResolver?.let {
                    phonesContentResolver.moveToFirst()
                    val phoneNumber = phonesContentResolver.getString(phonesContentResolver.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val contactName = phonesContentResolver.getString(phonesContentResolver.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

                    phoneNumberData.phoneNumber = phoneNumber
                    phoneNumberData.contactName = contactName

                    phonesContentResolver.close()
                }
            }
            contactsContentResolver.close()
        }
        return phoneNumberData
    }

    fun retrieveContactNameByPhoneNumber(phoneNumber: String): String {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val projection: Array<String> = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        var contactName = ""
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }
        return contactName
    }

}