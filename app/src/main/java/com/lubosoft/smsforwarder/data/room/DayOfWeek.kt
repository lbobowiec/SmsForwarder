package com.lubosoft.smsforwarder.data.room

enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    val value: Int
        get() = ordinal + 1

    companion object {
        private val ENUMS = values()

        fun of(dayOfWeek: Int): DayOfWeek {
            if (dayOfWeek < 1 || dayOfWeek > 7) {
                throw RuntimeException("Invalid value for DayOfWeek: $dayOfWeek")
            }
            return ENUMS[dayOfWeek - 1]
        }
    }
}