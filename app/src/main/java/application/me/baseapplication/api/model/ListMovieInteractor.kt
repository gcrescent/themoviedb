package application.me.baseapplication.api.model

import application.me.baseapplication.AppDatabase
import application.me.baseapplication.BaseApi
import application.me.baseapplication.BaseApplication
import application.me.baseapplication.BaseCallback
import application.me.baseapplication.api.service.GenreService
import application.me.baseapplication.api.service.MovieService
import id.paprika.paprika.api.exception.ApiError
import retrofit2.Call
import retrofit2.Response

class ListMovieInteractor(private val database: AppDatabase?) {

    fun fetchListMovies(page: Int, movieType: String, listener: MovieFinishedListener) {
        // if no genre saved, fetch from server
        if (database?.genreDao()?.getCount() ?: 0 == 0) {
            fetchGenres(object : GenreFinishedListener {
                override fun onSuccess() {
                    fetchMovies(page, movieType, listener)
                }

                override fun onFailed(error: ApiError) {
                    listener.onFailed(error)
                }

                override fun onFailed(t: Throwable) {
                    listener.onFailed(t)
                }
            })
        } else {
            fetchMovies(page, movieType, listener)
        }
    }

    private fun fetchMovies(page: Int, movieType: String, listener: MovieFinishedListener) {
        val service = BaseApi.getService(MovieService::class.java)
        service.getMovieList(movieType, page).enqueue(object : BaseCallback<MovieList>() {
            override fun onSuccess(call: Call<MovieList>, response: Response<MovieList>, t: MovieList?) {
                listener.onSuccess(setupMovies(t?.results))
            }

            override fun onFailure(call: Call<MovieList>, error: ApiError) {
                listener.onFailed(error)
            }

            override fun onFailure(call: Call<MovieList>, t: Throwable) {
                listener.onFailed(t)
            }
        })
    }

    fun getFavouriteMovies(): List<Movie>? {
        return setupMovies(database?.movieDao()?.findAll())
    }

    private fun fetchGenres(listener: GenreFinishedListener) {
        val genreService = BaseApi.getService(GenreService::class.java)
        genreService.getGenreList().enqueue(object : BaseCallback<GenreList>() {
            override fun onSuccess(call: Call<GenreList>, response: Response<GenreList>, t: GenreList?) {
                t?.genres?.let { genres ->
                    BaseApplication.database?.genreDao()?.insertAll(genres)
                }
                listener.onSuccess()
            }

            override fun onFailure(call: Call<GenreList>, error: ApiError) {
                listener.onFailed(error)
            }

            override fun onFailure(call: Call<GenreList>, t: Throwable) {
                listener.onFailed(t)
            }
        })
    }

    private fun setupMovies(movies: List<Movie>?): List<Movie>? {
        movies?.let {
            for (movie in it) {
                val genreNames = ArrayList<String>()
                movie.genreIds?.let { genreIds ->
                    val genreDao = BaseApplication.database?.genreDao()
                    for (genreId in genreIds) {
                        genreDao?.findNameById(genreId)?.let { genreName ->
                            genreNames.add(genreName)
                        }
                    }
                    movie.genreNames = genreNames.joinToString()
                }

                movie.isFavourite = checkIsFavourite(movie)
            }
        }
        return movies
    }

    private fun checkIsFavourite(movie: Movie): Boolean {
        return database?.movieDao()?.getCountById(movie.id) ?: 0 > 0
    }

    fun toggleFavourite(movie: Movie) {
        if (checkIsFavourite(movie)) {
            database?.movieDao()?.remove(movie.id)
        } else {
            database?.movieDao()?.insert(movie)
        }

        movie.isFavourite = checkIsFavourite(movie)
    }

    interface MovieFinishedListener {
        fun onSuccess(movies: List<Movie>?)
        fun onFailed(error: ApiError)
        fun onFailed(t: Throwable)
    }

    interface GenreFinishedListener {
        fun onSuccess()
        fun onFailed(error: ApiError)
        fun onFailed(t: Throwable)
    }
}