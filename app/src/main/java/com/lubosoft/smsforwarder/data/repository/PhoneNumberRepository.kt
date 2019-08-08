package com.lubosoft.smsforwarder.data.repository

import androidx.annotation.WorkerThread
import com.lubosoft.smsforwarder.data.room.PhoneNumberDao
import com.lubosoft.smsforwarder.data.room.PhoneNumberData
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class PhoneNumberRepository private constructor(private val phoneNumberDao: PhoneNumberDao) {

    fun getAllNumbers(isBlackList: Boolean = false) = phoneNumberDao.getAllNumbers(isBlackList)

    fun getPhoneNumber(id: Long) = phoneNumberDao.getPhoneNumber(id)

    @WorkerThread
    suspend fun insert(phoneNumberData: PhoneNumberData) {
        withContext(IO) {
            phoneNumberDao.insert(phoneNumberData)
        }
    }

    @WorkerThread
    suspend fun update(phoneNumberData: PhoneNumberData) {
        withContext(IO) {
            phoneNumberDao.update(phoneNumberData)
        }
    }

    @WorkerThread
    suspend fun delete(phoneNumberData: PhoneNumberData) {
        withContext(IO) {
            phoneNumberDao.delete(phoneNumberData)
        }
    }

    @WorkerThread
    suspend fun deleteAll(isBlackList: Boolean = false) {
        withContext(IO) {
            phoneNumberDao.deleteAllNumbers(isBlackList)
        }
    }

    companion object {

        @Volatile
        private var instance: PhoneNumberRepository? = null

        fun getInstance(phoneNumberDao: PhoneNumberDao) =
                instance ?: synchronized(this) {
                    instance ?: PhoneNumberRepository(phoneNumberDao).also { instance = it }
                }
    }

}
