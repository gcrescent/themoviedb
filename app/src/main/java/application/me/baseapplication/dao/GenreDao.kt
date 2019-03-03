package application.me.baseapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import application.me.baseapplication.api.model.Genre

@Dao
interface GenreDao {

    @Query("SELECT COUNT(id) FROM genre")
    fun getCount(): Int

    @Query("SELECT name FROM genre WHERE id = :id LIMIT 1")
    fun findNameById(id: Int): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(genres: List<Genre>)
}