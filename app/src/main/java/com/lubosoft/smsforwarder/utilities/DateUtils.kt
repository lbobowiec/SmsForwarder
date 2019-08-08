package com.lubosoft.smsforwarder.utilities

import android.content.Context
import android.text.format.DateFormat
import com.lubosoft.smsforwarder.R
import com.lubosoft.smsforwarder.data.room.DayOfWeek
import com.lubosoft.smsforwarder.data.room.LocalTime
import com.lubosoft.smsforwarder.data.room.TimeOffRule
import java.lang.IllegalArgumentException
import java.util.*

object DateUtils {

    private val daysOfWeekArray = arrayOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    private val calendarToDayOfWeekMap = mapOf(
        Calendar.MONDAY to DayOfWeek.MONDAY,
        Calendar.TUESDAY to DayOfWeek.TUESDAY,
        Calendar.WEDNESDAY to DayOfWeek.WEDNESDAY,
        Calendar.THURSDAY to DayOfWeek.THURSDAY,
        Calendar.FRIDAY to DayOfWeek.FRIDAY,
        Calendar.SATURDAY to DayOfWeek.SATURDAY,
        Calendar.SUNDAY to DayOfWeek.SUNDAY
    )

    private fun daysOfWeekToDayNames(context: Context, daysOfWeek: List<DayOfWeek>): List<String> {
        val result = mutableListOf<String>()
        for (day in daysOfWeek) {
            when (day) {
                DayOfWeek.MONDAY -> result.add(context.getString(R.string.monday))
                DayOfWeek.TUESDAY -> result.add(context.getString(R.string.tuesday))
                DayOfWeek.WEDNESDAY -> result.add(context.getString(R.string.wednesday))
                DayOfWeek.THURSDAY -> result.add(context.getString(R.string.thursday))
                DayOfWeek.FRIDAY -> result.add(context.getString(R.string.friday))
                DayOfWeek.SATURDAY -> result.add(context.getString(R.string.saturday))
                DayOfWeek.SUNDAY -> result.add(context.getString(R.string.sunday))
            }
        }
        return result
    }

    fun daysOfWeekToString(context: Context, daysOfWeek: List<DayOfWeek>): String {
        val result = daysOfWeekToDayNames(context, daysOfWeek)
        return result.joinToString { it }
    }

    fun isDateWithinTimeIntervals(date: Date, timeOffRules: List<TimeOffRule>, is24Clock: Boolean = false): Boolean {
        for (timeOffRule in timeOffRules) {
            if (isWithinDateRanges(date, timeOffRule, is24Clock)) {
                return true
            }
        }
        return false
    }

    fun isWithinDateRanges(date: Date, timeOffRule: TimeOffRule, is24Clock: Boolean = false): Boolean {
        val startTime = timeOffRule.startTime
        val endTime = timeOffRule.endTime
        for (dayOfWeek in timeOffRule.daysOfWeek) {
            if (isBetween(date, dayOfWeek, startTime, endTime, is24Clock)) {
                return true
            }
        }
        return false
    }

    fun isBetween(date: Date, dayOfWeek: DayOfWeek, startTime: LocalTime, endTime: LocalTime, is24Clock: Boolean = false): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day= calendar.get(Calendar.DAY_OF_WEEK);
        val currentDayOfWeek = calendarToDayOfWeekMap[day] ?: throw IllegalArgumentException("$day is out of range of days of week")
        val currentHour = if (is24Clock) calendar.get(Calendar.HOUR_OF_DAY) else calendar.get(Calendar.HOUR)
        val currentMinute = calendar.get(Calendar.MINUTE)

        if (isLessOrEqual(startTime, endTime)) {
            if (currentDayOfWeek == dayOfWeek &&
               (startTime.hour < currentHour || (startTime.hour == currentHour && startTime.minute <= currentMinute)) &&
               (currentHour < endTime.hour || (currentHour == endTime.hour && currentMinute <= endTime.minute))) {
                return true
            }
        } else {
            if ((currentDayOfWeek == dayOfWeek &&
                (startTime.hour < currentHour || (startTime.hour == currentHour && startTime.minute <= currentMinute))) ||
                (currentDayOfWeek == nextDayOfWeek(dayOfWeek) &&
                (currentHour < endTime.hour || (currentHour == endTime.hour && currentMinute <= endTime.minute)))) {
                return true
            }
        }
        return false
    }

    fun isLessOrEqual(startTime: LocalTime, endTime: LocalTime, is24Clock: Boolean = false): Boolean {
        if (is24Clock) {
            return lessOrEqual(startTime, endTime)
        } else {
            return if (startTime.amPm == endTime.amPm) lessOrEqual(startTime, endTime) else (endTime.amPm == "PM")
        }
    }

    private fun lessOrEqual(startTime: LocalTime, endTime: LocalTime): Boolean {
        return (startTime.hour < endTime.hour || (startTime.hour == endTime.hour && startTime.minute <= endTime.minute))
    }

    private fun nextDayOfWeek(dayOfWeek: DayOfWeek) = daysOfWeekArray[dayOfWeek.ordinal + 1 % 7]

    fun adjustTimeToDefaultLocale(context: Context, localTime: LocalTime): LocalTime {
        val result = localTime
        if (DateFormat.is24HourFormat(context)) {
            if (localTime.amPm.isNotEmpty()) {
                if (localTime.amPm == "PM") {
                    result.hour += 12
                }
                result.amPm = ""
            }
        } else {
            if (localTime.amPm.isEmpty()) {
                result.amPm = "AM"
                if (localTime.hour > 11) {
                    result.hour -= 12
                    result.amPm = "PM"
                }
            }
        }
        return result
    }

}