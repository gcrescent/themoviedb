package application.me.baseapplication

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun toString(value: List<Int>?): String? {
        return if (value == null) null else value.joinToString()
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.split(',')?.map {
            it.trim().toInt()
        }
    }
}