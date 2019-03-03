package application.me.baseapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import application.me.baseapplication.api.model.Genre
import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.dao.GenreDao
import application.me.baseapplication.dao.MovieDao

@Database(entities = [Genre::class, Movie::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun genreDao(): GenreDao

    abstract fun movieDao(): MovieDao

}