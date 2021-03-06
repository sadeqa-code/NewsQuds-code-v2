package com.sadeqa.jamal.newsquds.db

import androidx.room.TypeConverter
import com.sadeqa.jamal.newsquds.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}