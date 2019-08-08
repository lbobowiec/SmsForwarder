package com.lubosoft.smsforwarder.data.room

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    companion object {

        private const val HOUR_MINUTE_SEPARATOR = ":"

        private const val DAYS_OF_WEEK_SEPARATOR = ","

        @TypeConverter
        @JvmStatic
        fun fromLocalTime(value: LocalTime): String {
            val c = Calendar.getInstance()
            return if (value.amPm.isEmpty()) {
                c.set(Calendar.HOUR_OF_DAY, value.hour)
                c.set(Calendar.MINUTE, value.minute)
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(c.time)
            } else {
                if (value.amPm == "AM") {
                    c.set(Calendar.AM_PM, Calendar.AM)
                } else {
                    c.set(Calendar.AM_PM, Calendar.PM)
                }
                c.set(Calendar.HOUR, value.hour)
                c.set(Calendar.MINUTE, value.minute)
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(c.time)
            }
        }

        @TypeConverter
        @JvmStatic
        fun stringToLocalTime(value: String): LocalTime {
            val tokens = value.split(" ")
            var amPM = ""
            if (tokens.size > 1) {
                amPM = tokens[1]
            }
            val (hour, minute) = tokens[0].split(HOUR_MINUTE_SEPARATOR)
            return LocalTime(hour.toInt(), minute.toInt(), amPM)
        }

        @TypeConverter
        @JvmStatic
        fun daysOfWeekToString(daysOfWeek: MutableList<DayOfWeek>): String {
            return daysOfWeek.map { it.value }.joinToString(separator = DAYS_OF_WEEK_SEPARATOR)
        }

        @TypeConverter
        @JvmStatic
        fun stringToDaysOfWeek(daysOfWeek: String): MutableList<DayOfWeek> {
            return daysOfWeek.split(DAYS_OF_WEEK_SEPARATOR).map { DayOfWeek.of(it.toInt()) }.toMutableList()
        }

    }

}