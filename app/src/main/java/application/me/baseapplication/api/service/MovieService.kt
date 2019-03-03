package application.me.baseapplication.api.service

import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.api.model.MovieList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("movie/{type}")
    fun getMovieList(
        @Path("type") movieType: String,
        @Query("page") page: Int
    ): Call<MovieList>

    @GET("movie/{id}")
    fun getMovie(
        @Path("id") id: Int
    ): Call<Movie>
}