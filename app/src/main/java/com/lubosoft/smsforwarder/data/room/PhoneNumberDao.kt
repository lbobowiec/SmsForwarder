package com.lubosoft.smsforwarder.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PhoneNumberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneNumberData: PhoneNumberData): Long

    @Update
    fun update(vararg phoneNumberData: PhoneNumberData)

    @Delete
    fun delete(phoneNumberData: PhoneNumberData)

    @Query("DELETE FROM phone_numbers WHERE black_list = :isBlackList")
    fun deleteAllNumbers(isBlackList: Boolean = false)

    @Query("SELECT * FROM phone_numbers WHERE id = :id")
    fun getPhoneNumber(id: Long): LiveData<PhoneNumberData>

    @Query("SELECT * FROM phone_numbers WHERE black_list = :isBlackList")
    fun getAllNumbers(isBlackList: Boolean = false): LiveData<List<PhoneNumberData>>

    @Query("SELECT phone_number FROM phone_numbers WHERE black_list = :isBlackList")
    fun getPhoneNumbers(isBlackList: Boolean = false): List<String>
}