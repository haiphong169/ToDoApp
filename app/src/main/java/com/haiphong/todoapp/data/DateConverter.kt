package com.haiphong.todoapp.data

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DateConverter {
    @TypeConverter
    fun epochSecondFromDate(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toEpochSecond()
    }

    @TypeConverter
    fun dateFromEpochSecond(epochSecond: Long?): LocalDateTime? {
        return if (epochSecond != null) {
            LocalDateTime.ofInstant(
                Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault()
            )
        } else {
            null
        }
    }
}