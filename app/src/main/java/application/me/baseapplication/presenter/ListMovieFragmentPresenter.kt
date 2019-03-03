package application.me.baseapplication.presenter

import application.me.baseapplication.BaseApi
import application.me.baseapplication.BaseApplication
import application.me.baseapplication.BaseCallback
import application.me.baseapplication.api.model.GenreList
import application.me.baseapplication.api.model.ListMovieInteractor
import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.api.service.GenreService
import application.me.baseapplication.ui.ListMovieFragment
import id.paprika.paprika.api.exception.ApiError
import retrofit2.Call
import retrofit2.Response

class ListMovieFragmentPresenter(private val view: View, private val interactor: ListMovieInteractor) {

    fun fetchListMovies(page: Int, movieType: String) {
        if (movieType != ListMovieFragment.FAVOURITE_MOVIE) {
            view.setAdapterLoading()
            interactor.fetchListMovies(page, movieType, object : ListMovieInteractor.MovieFinishedListener {
                override fun onSuccess(movies: List<Movie>?) {
                    view.fetchSuccess(movies, page == 1)
                }

                override fun onFailed(error: ApiError) {
                    view.fetchFailed(error)
                }

                override fun onFailed(t: Throwable) {
                    view.fetchFailed(t)
                }
            })
        } else {
            view.fetchSuccess(interactor.getFavouriteMovies(), true)
        }
    }

    fun toggleFavourite(movie: Movie, pos: Int) {
        interactor.toggleFavourite(movie)
        view.onToggleFavourite(pos)
    }

    interface View {
        fun setAdapterLoading()
        fun fetchSuccess(movies: List<Movie>?, clear: Boolean)
        fun fetchFailed(error: ApiError)
        fun fetchFailed(t: Throwable)
        fun onToggleFavourite(pos: Int)
    }
}