package application.me.baseapplication.presenter

import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.api.model.MovieDetailInteractor
import id.paprika.paprika.api.exception.ApiError

class MovieDetailActivityPresenter(private val view: View, private val interactor: MovieDetailInteractor) {

    fun fetchMovie(movie: Movie) {
        if (movie.getBackdropUrl().isNullOrEmpty()) {
            view.showLoading()
        } else {
            view.hideLoading()
        }
        interactor.fetchMovie(movie.id, object : MovieDetailInteractor.OnFinishedListener {
            override fun onSuccess(movie: Movie?) {
                movie?.let {
                    view.hideLoading()
                    view.setupView(it)
                }
            }

            override fun onFailed(error: ApiError) {
                view.showLoadingError(error)
            }

            override fun onFailed(t: Throwable) {
                view.showLoadingError(t)
            }
        })
    }

    fun toggleFavorite(movie: Movie) {
        interactor.toggleFavourite(movie)
        view.setFavouriteButtonState(movie.isFavourite)
    }

    interface View {
        fun setupView(movie: Movie)
        fun showLoading()
        fun hideLoading()
        fun showLoadingError(error: ApiError)
        fun showLoadingError(t: Throwable)
        fun setFavouriteButtonState(isFavourite: Boolean)
    }
}