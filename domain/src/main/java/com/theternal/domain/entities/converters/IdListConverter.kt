package com.theternal.domain.entities.converters

import androidx.room.TypeConverter

class IdListConverter {
    @TypeConverter
    fun fromStringToList(value: String): List<Long> {
        if(value.isEmpty()) return listOf()
        return value.split(splitter).map { it.toLong() }
    }

    @TypeConverter
    fun fromListToString(value: List<Long>): String {
        return value.joinToString(splitter) { it.toString() }
    }

    companion object {
        private const val splitter = "~~"
    }
}