package application.me.baseapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import application.me.baseapplication.*
import application.me.baseapplication.api.model.GenreList
import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.api.model.MovieList
import application.me.baseapplication.api.service.GenreService
import application.me.baseapplication.api.service.MovieService
import application.me.baseapplication.ui.ListMovieFragment
import com.bumptech.glide.Glide
import id.paprika.paprika.api.exception.ApiError
import kotlinx.android.synthetic.main.item_movie.view.*
import retrofit2.Call
import retrofit2.Response

class MovieAdapter(context: Context, private val sortBy: String) : BaseRecyclerAdapter<Movie>(context) {

    override fun fetchData(page: Int, limit: Int, callback: OnQueryFinishListener<Movie>) {
        if (BaseApplication.database?.genreDao()?.getCount() ?: 0 == 0) {
            val genreService = BaseApi.getService(GenreService::class.java)
            genreService.getGenreList().enqueue(object : BaseCallback<GenreList>(context) {
                override fun onSuccess(call: Call<GenreList>, response: Response<GenreList>, t: GenreList?) {
                    t?.genres?.let { genres ->
                        BaseApplication.database?.genreDao()?.insertAll(genres)
                    }
                    fetchMovie(page, limit, callback)
                }

                override fun onFailure(call: Call<GenreList>, error: ApiError) {
                    callback.failed(error)
                }

                override fun onFailure(call: Call<GenreList>, t: Throwable) {
                    callback.failed(t)
                }
            })
        } else {
            fetchMovie(page, limit, callback)
        }
    }

    private fun fetchMovie(page: Int, limit: Int, callback: OnQueryFinishListener<Movie>) {
        if (sortBy != ListMovieFragment.FAVOURITE_MOVIE) {
            val service = BaseApi.getService(MovieService::class.java)
            service.getMovieList(sortBy, page).enqueue(object : BaseCallback<MovieList>(context) {
                override fun onSuccess(call: Call<MovieList>, response: Response<MovieList>, t: MovieList?) {
                    callback.success(t?.results)
                }

                override fun onFailure(call: Call<MovieList>, error: ApiError) {
                    callback.failed(error)
                }

                override fun onFailure(call: Call<MovieList>, t: Throwable) {
                    callback.failed(t)
                }
            })
        } else {
            callback.success(BaseApplication.database?.movieDao()?.findAll())
        }
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false))
    }

    override fun onBindItemViewHolder(baseViewHolder: BaseViewHolder, t: Movie?, position: Int) {
        t?.let {
            (baseViewHolder as MovieViewHolder).onBind(context, t)
            baseViewHolder.setOnClickListener(View.OnClickListener { _ ->
                getItemClickListener().onItemClick(it)
            })
        }
    }

    override fun onBindErrorViewHolder(errorView: View) {

    }

    override fun onBindEmptyViewHolder(emptyView: View) {

    }

    class MovieViewHolder(view: View) : BaseViewHolder(view) {

        fun onBind(context: Context, movie: Movie) {
            Glide.with(itemView)
                .load(movie.getPosterUrl())
                .into(itemView.moviePoster)

            itemView.movieTitle.text = movie.originalTitle
            itemView.movieGenres.text = movie.getGenreNames()
            itemView.movieFavourite.setOnClickListener {
                toggleFavorite(context, movie)
            }

            favouriteButtonState(context, movie)
        }

        private fun toggleFavorite(context: Context, movie: Movie) {
            // save to local because the api need themoviedb account to make favourite list
            if (movie.isFavourite()) {
                BaseApplication.database?.movieDao()?.remove(movie.id)
            } else {
                BaseApplication.database?.movieDao()?.insert(movie)
            }
            favouriteButtonState(context, movie)
        }

        private fun favouriteButtonState(context: Context, movie: Movie) {
            if (movie.isFavourite()) {
                itemView.movieFavourite.setColorFilter(ContextCompat.getColor(context, R.color.favourite))
            } else {
                itemView.movieFavourite.setColorFilter(ContextCompat.getColor(context, R.color.not_favourite))
            }
        }
    }
}