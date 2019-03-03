package application.me.baseapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import application.me.baseapplication.R
import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.ui.ListMovieFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.empty_view.view.*
import kotlinx.android.synthetic.main.item_movie.view.*

class MovieAdapter(context: Context, private val movieType: String) : BaseRecyclerAdapter<Movie>(context) {

    var onFavouriteClickListener: OnFavouriteClickedListener? = null

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false))
    }

    override fun onBindItemViewHolder(baseViewHolder: BaseViewHolder, t: Movie?, position: Int) {
        t?.let { movie ->
            (baseViewHolder as MovieViewHolder).onBind(context, t)
            baseViewHolder.setOnClickListener(View.OnClickListener { _ ->
                getItemClickListener().onItemClick(movie)
            })
            baseViewHolder.itemView.movieFavourite.setOnClickListener {
                onFavouriteClickListener?.onFavouriteClicked(movie, position)
            }
        }
    }

    override fun onBindErrorViewHolder(errorView: View) {

    }

    override fun onBindEmptyViewHolder(emptyView: View) {
        if (movieType == ListMovieFragment.FAVOURITE_MOVIE) {
            emptyView.emptyMessage.text = context.getString(R.string.empty_favourite_list)
        }
    }

    interface OnFavouriteClickedListener {
        fun onFavouriteClicked(movie: Movie, position: Int)
    }

    class MovieViewHolder(view: View) : BaseViewHolder(view) {

        fun onBind(context: Context, movie: Movie) {
            Glide.with(itemView)
                .load(movie.getPosterUrl())
                .into(itemView.moviePoster)

            itemView.movieTitle.text = movie.originalTitle
            itemView.movieGenres.text = movie.genreNames

            favouriteButtonState(context, movie)
        }

        private fun favouriteButtonState(context: Context, movie: Movie) {
            if (movie.isFavourite) {
                itemView.movieFavourite.setColorFilter(ContextCompat.getColor(context, R.color.favourite))
            } else {
                itemView.movieFavourite.setColorFilter(ContextCompat.getColor(context, R.color.not_favourite))
            }
        }
    }
}