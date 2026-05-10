package com.harc.health.repository.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harc.health.model.MatrixPhase
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromIntList(value: String?): List<Int>? {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromIntListToString(list: List<Int>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringDoubleMap(value: String?): Map<String, Double>? {
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, Double>?): String? {
        return Gson().toJson(map)
    }

    @TypeConverter
    fun fromMatrixPhase(value: MatrixPhase): String {
        return value.name
    }

    @TypeConverter
    fun toMatrixPhase(value: String): MatrixPhase {
        return MatrixPhase.valueOf(value)
    }
}
