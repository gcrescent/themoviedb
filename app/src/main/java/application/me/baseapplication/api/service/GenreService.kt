package application.me.baseapplication.api.service

import application.me.baseapplication.api.model.GenreList
import retrofit2.Call
import retrofit2.http.GET

interface GenreService {

    @GET("genre/movie/list")
    fun getGenreList(): Call<GenreList>
}