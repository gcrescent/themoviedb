package application.me.baseapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import application.me.baseapplication.api.model.Movie

@Dao
interface MovieDao {

    @Query("SELECT COUNT(id) FROM movie WHERE id = :id")
    fun getCountById(id: Int): Int

    @Query("SELECT * FROM movie")
    fun findAll(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie)

    @Query("DELETE FROM movie WHERE id = :id")
    fun remove(id: Int)
}