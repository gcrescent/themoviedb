package application.me.baseapplication

import android.app.Application
import androidx.room.Room
import application.me.baseapplication.api.service.GenreService
import application.me.baseapplication.api.service.MovieService

class BaseApplication : Application() {

    companion object {
        var database: AppDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        BaseApplication.database = Room.databaseBuilder(this, AppDatabase::class.java, "the-movie-db")
            .allowMainThreadQueries()
            .build()

        BaseApi.initiate()

        // Register services here
        BaseApi.registerService(MovieService::class.java)
        BaseApi.registerService(GenreService::class.java)
    }
}