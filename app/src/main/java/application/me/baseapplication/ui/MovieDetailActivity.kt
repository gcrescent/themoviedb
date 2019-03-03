package application.me.baseapplication.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import application.me.baseapplication.*
import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.api.service.MovieService
import application.me.baseapplication.helper.ErrorHandler
import com.bumptech.glide.Glide
import id.paprika.paprika.api.exception.ApiError
import kotlinx.android.synthetic.main.activity_movie_detail.*
import retrofit2.Call
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailActivity : BaseActivity() {

    companion object {

        private const val INTENT_MOVIE = "movie"

        fun newIntent(context: Context, movie: Movie): Intent {
            val intent = Intent(context, MovieDetailActivity::class.java)
            intent.putExtra(INTENT_MOVIE, movie)
            return intent
        }
    }

    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE)
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT)

        movie = intent.getParcelableExtra(INTENT_MOVIE)

        movie?.let { movie ->
            setupView(movie)
            fetchMovie(movie.id)
        }
    }

    private fun fetchMovie(id: Int) {
        if (movie?.getBackdropUrl().isNullOrEmpty()) {
            loadingView.setLoading()
            loadingView.setErrorClickListener(View.OnClickListener {
                fetchMovie(id)
            })
        } else {
            loadingView.setSuccess()
        }
        val service = BaseApi.getService(MovieService::class.java)
        service.getMovie(id).enqueue(object : BaseCallback<Movie>(this) {
            override fun onSuccess(call: Call<Movie>, response: Response<Movie>, t: Movie?) {
                loadingView.setSuccess()
                t?.let { movie ->
                    this@MovieDetailActivity.movie = movie
                    setupView(movie)
                }
            }

            override fun onFailure(call: Call<Movie>, error: ApiError) {
                loadingView.setError(ErrorHandler.getErrorMessage(this@MovieDetailActivity, error))
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                loadingView.setError(ErrorHandler.getErrorMessage(this@MovieDetailActivity, t))
            }
        })
    }

    private fun setupView(movie: Movie) {
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
        favouriteButtonState(movie)
        movieFavourite.setOnClickListener {
            toggleFavorite(movie)
        }
    }

    private fun toggleFavorite(movie: Movie) {
        if (movie.isFavourite()) {
            BaseApplication.database?.movieDao()?.remove(movie.id)
        } else {
            BaseApplication.database?.movieDao()?.insert(movie)
        }
        favouriteButtonState(movie)
    }

    private fun favouriteButtonState(movie: Movie) {
        if (movie.isFavourite()) {
            movieFavourite.setColorFilter(ContextCompat.getColor(this, R.color.favourite))
        } else {
            movieFavourite.setColorFilter(ContextCompat.getColor(this, R.color.not_favourite))
        }
    }
}