package com.lubosoft.smsforwarder.data.repository

import androidx.annotation.WorkerThread
import com.lubosoft.smsforwarder.data.room.TimeOffRule
import com.lubosoft.smsforwarder.data.room.TimeOffRuleDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class TimeOffRuleRepository private constructor(private val timeOffRuleDao: TimeOffRuleDao){

    fun getAll() = timeOffRuleDao.getAll()

    fun getTimeOffRule(id: Long) = timeOffRuleDao.getTimeOffRule(id)

    @WorkerThread
    suspend fun insert(timeOffRule: TimeOffRule) {
        withContext(IO) {
            timeOffRuleDao.insert(timeOffRule)
        }
    }

    @WorkerThread
    suspend fun update(timeOffRule: TimeOffRule) {
        withContext(IO) {
            timeOffRuleDao.update(timeOffRule)
        }
    }

    @WorkerThread
    suspend fun delete(timeOffRule: TimeOffRule) {
        withContext(IO) {
            timeOffRuleDao.delete(timeOffRule)
        }
    }

    @WorkerThread
    suspend fun deleteAll() {
        withContext(IO) {
            timeOffRuleDao.deleteAll()
        }
    }

    companion object {

        @Volatile
        private var instance: TimeOffRuleRepository? = null

        fun getInstance(timeOffRuleDao: TimeOffRuleDao) =
                instance ?: synchronized(this) {
                    instance ?: TimeOffRuleRepository(timeOffRuleDao).also { instance = it }
                }
    }
}