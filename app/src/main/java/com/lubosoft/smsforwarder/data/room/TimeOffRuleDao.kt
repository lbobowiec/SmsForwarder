package com.lubosoft.smsforwarder.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimeOffRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timeOffRule: TimeOffRule): Long

    @Update
    fun update(vararg timeOffRule: TimeOffRule)

    @Delete
    fun delete(timeOffRule: TimeOffRule)

    @Query("DELETE FROM timeoff_rules")
    fun deleteAll()

    @Query("SELECT * FROM timeoff_rules WHERE id = :id")
    fun getTimeOffRule(id: Long): LiveData<TimeOffRule>

    @Query("SELECT * FROM timeoff_rules")
    fun getAll(): LiveData<List<TimeOffRule>>

    @Query("SELECT * FROM timeoff_rules")
    fun getAllTimeOffRules(): List<TimeOffRule>
}