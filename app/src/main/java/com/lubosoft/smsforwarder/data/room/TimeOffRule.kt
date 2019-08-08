package com.lubosoft.smsforwarder.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timeoff_rules")
data class TimeOffRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name="days_of_week") var daysOfWeek: MutableList<DayOfWeek>,
    @ColumnInfo(name="start_time") var startTime: LocalTime,
    @ColumnInfo(name="end_time") var endTime: LocalTime
)