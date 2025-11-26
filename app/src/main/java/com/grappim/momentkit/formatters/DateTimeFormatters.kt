package com.grappim.momentkit.formatters

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

object DateTimeFormatters {

    // Time formatters
    private val time24HourFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val time12HourFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a")

    // Time formatters without seconds (for widget)
    private val time24HourFormatterNoSeconds = DateTimeFormatter.ofPattern("HH:mm")
    private val time12HourFormatterNoSeconds = DateTimeFormatter.ofPattern("hh:mm a")

    // Date formatters
    private val dateDDMMYYYYFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val dateMMDDYYYYFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private val dateYYYYMMDDFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    // Additional formatters
    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM")

    // Time formatting functions
    fun format24Hour(dateTime: LocalDateTime): String = dateTime.format(time24HourFormatter)

    fun format12Hour(dateTime: LocalDateTime): String = dateTime.format(time12HourFormatter)

    fun format24HourNoSeconds(dateTime: LocalDateTime): String =
        dateTime.format(time24HourFormatterNoSeconds)

    fun format12HourNoSeconds(dateTime: LocalDateTime): String =
        dateTime.format(time12HourFormatterNoSeconds)

    // Date formatting functions
    fun formatDDMMYYYY(dateTime: LocalDateTime): String = dateTime.format(dateDDMMYYYYFormatter)

    fun formatMMDDYYYY(dateTime: LocalDateTime): String = dateTime.format(dateMMDDYYYYFormatter)

    fun formatYYYYMMDD(dateTime: LocalDateTime): String = dateTime.format(dateYYYYMMDDFormatter)

    // Additional info functions
    fun getWeekOfYear(dateTime: LocalDateTime): Int =
        dateTime.get(WeekFields.of(Locale.getDefault()).weekOfYear())

    fun getDayOfYear(dateTime: LocalDateTime): Int = dateTime.dayOfYear

    fun getDayOfWeek(dateTime: LocalDateTime): String = dateTime.format(dayOfWeekFormatter)

    fun getMonth(dateTime: LocalDateTime): String = dateTime.format(monthFormatter)

    fun getMonthOfYear(dateTime: LocalDateTime): Int = dateTime.monthValue
}
