package application.me.baseapplication.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import application.me.baseapplication.BaseActivity
import application.me.baseapplication.R
import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.api.model.MovieDetailInteractor
import application.me.baseapplication.helper.ErrorHandler
import application.me.baseapplication.presenter.MovieDetailActivityPresenter
import com.bumptech.glide.Glide
import id.paprika.paprika.api.exception.ApiError
import application.me.baseapplication.BaseApplication
import kotlinx.android.synthetic.main.activity_movie_detail.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailActivity : BaseActivity(), MovieDetailActivityPresenter.View {

    companion object {

        private const val INTENT_MOVIE = "movie"

        fun newIntent(context: Context, movie: Movie): Intent {
            val intent = Intent(context, MovieDetailActivity::class.java)
            intent.putExtra(INTENT_MOVIE, movie)
            return intent
        }
    }

    private lateinit var presenter: MovieDetailActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        presenter = MovieDetailActivityPresenter(this, MovieDetailInteractor(BaseApplication.database?.movieDao()))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE)
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT)

        val movie: Movie? = intent.getParcelableExtra(INTENT_MOVIE)

        movie?.let {
            setupView(it)
            presenter.fetchMovie(it)

            loadingView.setErrorClickListener(View.OnClickListener { _ ->
                presenter.fetchMovie(it)
            })
        }
    }

    override fun setupView(movie: Movie) {
        toolbar.title = movie.originalTitle

        Glide.with(this@MovieDetailActivity)
            .load(movie.getBackdropUrl())
            .into(movieBackdrop)

        Glide.with(this@MovieDetailActivity)
            .load(movie.getPosterUrl())
            .into(moviePoster)

        movieTitle.text = movie.originalTitle
        movieRating.text =
                getString(R.string.rating_placeholder, DecimalFormat("0.#").format(movie.voteAverage))
        movieVotes.text =
                resources.getQuantityString(R.plurals.votes_placeholder, movie.voteCount, movie.voteCount)
        movie.releaseDate?.let {
            movieReleaseDate.text = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(it)
        }
        movieLanguage.text = movie.originalLanguage
        if (movie.overview.isNullOrBlank()) {
            movieOverviewContainer.visibility = View.GONE
        } else {
            movieOverviewContainer.visibility = View.VISIBLE
            movieOverview.text = movie.overview
        }
        setFavouriteButtonState(movie.isFavourite)
        movieFavourite.setOnClickListener {
            presenter.toggleFavorite(movie)
        }

    }

    override fun showLoading() {
        loadingView.setLoading()
    }

    override fun hideLoading() {
        loadingView.setSuccess()
    }

    override fun showLoadingError(error: ApiError) {
        loadingView.setError(ErrorHandler.getErrorMessage(this, error))
    }

    override fun showLoadingError(t: Throwable) {
        loadingView.setError(ErrorHandler.getErrorMessage(this, t))
    }

    override fun setFavouriteButtonState(isFavourite: Boolean) {
        if (isFavourite) {
            movieFavourite.setColorFilter(ContextCompat.getColor(this, R.color.favourite))
        } else {
            movieFavourite.setColorFilter(ContextCompat.getColor(this, R.color.not_favourite))
        }
    }
}