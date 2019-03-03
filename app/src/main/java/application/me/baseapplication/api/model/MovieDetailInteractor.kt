package application.me.baseapplication.api.model

import application.me.baseapplication.BaseApi
import application.me.baseapplication.BaseApplication
import application.me.baseapplication.BaseCallback
import application.me.baseapplication.api.service.MovieService
import application.me.baseapplication.dao.MovieDao
import id.paprika.paprika.api.exception.ApiError
import retrofit2.Call
import retrofit2.Response

class MovieDetailInteractor(private val movieDao: MovieDao?) {

    fun fetchMovie(id: Int, listener: OnFinishedListener) {
        val service = BaseApi.getService(MovieService::class.java)
        service.getMovie(id).enqueue(object : BaseCallback<Movie>() {
            override fun onSuccess(call: Call<Movie>, response: Response<Movie>, t: Movie?) {
                listener.onSuccess(setupMovie(t))
            }

            override fun onFailure(call: Call<Movie>, error: ApiError) {
                listener.onFailed(error)
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                listener.onFailed(t)
            }
        })
    }

    private fun setupMovie(movie: Movie?): Movie? {
        movie?.let {
            val genreNames = ArrayList<String>()
            movie.genres?.let { genres ->
                for (genre in genres) {
                    genre.name?.let {
                        genreNames.add(it)
                    }
                }
                movie.genreNames = genreNames.joinToString()
            }
            movie.isFavourite = checkIsFavourite(movie)
        }
        return movie
    }

    private fun checkIsFavourite(movie: Movie): Boolean {
        return movieDao?.getCountById(movie.id) ?: 0 > 0
    }

    fun toggleFavourite(movie: Movie) {
        if (checkIsFavourite(movie)) {
            movieDao?.remove(movie.id)
        } else {
            movieDao?.insert(movie)
        }

        setupMovie(movie)
    }

    interface OnFinishedListener {
        fun onSuccess(movie: Movie?)
        fun onFailed(error: ApiError)
        fun onFailed(t: Throwable)
    }
}