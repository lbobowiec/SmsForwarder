package com.lubosoft.smsforwarder.data.room

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phone_numbers")
data class PhoneNumberData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name="phone_number") @NonNull var phoneNumber: String,
    @ColumnInfo(name="contact_name") var contactName: String = "",
    @ColumnInfo(name="black_list") var isBlackList: Boolean = false
)
