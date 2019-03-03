package application.me.baseapplication.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import application.me.baseapplication.BaseApplication
import application.me.baseapplication.BaseFragment
import application.me.baseapplication.R
import application.me.baseapplication.adapter.BaseRecyclerAdapter
import application.me.baseapplication.adapter.MovieAdapter
import application.me.baseapplication.api.model.ListMovieInteractor
import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.helper.EndlessRecyclerOnScrollListener
import application.me.baseapplication.helper.ErrorHandler
import application.me.baseapplication.helper.GridSpacingItemDecoration
import application.me.baseapplication.presenter.ListMovieFragmentPresenter
import application.me.baseapplication.toPx
import com.google.android.material.snackbar.Snackbar
import id.paprika.paprika.api.exception.ApiError
import kotlinx.android.synthetic.main.fragment_list_movie.*

class ListMovieFragment : BaseFragment(), ListMovieFragmentPresenter.View {

    companion object {

        const val POPULAR_MOVIES = "popular"
        const val TOP_RATED_MOVIE = "top_rated"
        const val FAVOURITE_MOVIE = "favourite"

        private const val INTENT_MOVIE_TYPE = "movieType"

        fun newInstance(movieType: String): Fragment {
            val fragment = ListMovieFragment()
            val args = Bundle()
            args.putString(INTENT_MOVIE_TYPE, movieType)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var presenter: ListMovieFragmentPresenter
    private lateinit var adapter: MovieAdapter
    private var movieType: String = POPULAR_MOVIES

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_movie, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = ListMovieFragmentPresenter(this, ListMovieInteractor(BaseApplication.database))
        movieType = arguments?.getString(INTENT_MOVIE_TYPE, POPULAR_MOVIES) ?: POPULAR_MOVIES
        adapter = MovieAdapter(requireContext(), movieType)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    BaseRecyclerAdapter.VIEW_TYPE_PROGRESS -> 2
                    BaseRecyclerAdapter.VIEW_TYPE_ERROR -> 2
                    BaseRecyclerAdapter.VIEW_TYPE_EMPTY -> 2
                    BaseRecyclerAdapter.VIEW_TYPE_ITEM -> 1
                    else -> 1
                }
            }
        }
        movieList.layoutManager = layoutManager
        movieList.addItemDecoration(GridSpacingItemDecoration(2, 4.toPx, true))
        movieList.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                // load nextPage
                if (adapter.hasNextPage && !adapter.loading) {
                    presenter.fetchListMovies(adapter.currentPage + 1, movieType)
                }
            }
        })

        swipeLayout.setOnRefreshListener {
            reloadData()
        }

        setupAdapter()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        reloadData()
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    private fun setupAdapter() {
        if (movieType != FAVOURITE_MOVIE) {
            adapter.setPaginationEnabled(true)
        }
        adapter.setErrorClickListener(object : BaseRecyclerAdapter.OnErrorClickListener {
            override fun onErrorClick() {
                reloadData()
            }
        })
        adapter.setItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener<Movie> {
            override fun onItemClick(t: Movie) {
                activity?.let {
                    startActivity(MovieDetailActivity.newIntent(it, t))
                }
            }
        })

        adapter.onFavouriteClickListener = object : MovieAdapter.OnFavouriteClickedListener {
            override fun onFavouriteClicked(movie: Movie, position: Int) {
                presenter.toggleFavourite(movie, position)
            }
        }
        movieList.adapter = adapter
    }

    fun reloadData() {
        // to make sure reload data every tab change
        if (userVisibleHint && isResumed) {
            swipeLayout?.isRefreshing = true
            adapter.currentPage = 1
            presenter.fetchListMovies(1, movieType)
        }
    }

    override fun setAdapterLoading() {
        adapter.loading = true
    }

    override fun fetchSuccess(movies: List<Movie>?, clear: Boolean) {
        movies?.let {
            if (clear) {
                adapter.setObjects(movies)
            } else {
                adapter.addObjects(movies)
            }
        }
        swipeLayout?.isRefreshing = false
    }

    override fun fetchFailed(error: ApiError) {
        handleAdapterError(ErrorHandler.getErrorMessage(activity, error))
    }

    override fun fetchFailed(t: Throwable) {
        handleAdapterError(ErrorHandler.getErrorMessage(activity, t))
    }

    private fun handleAdapterError(message: String) {
        adapter.loading = false
        if (adapter.getObjects().isEmpty()) {
            adapter.error = true
            adapter.errorMessage = message
            adapter.notifyDataSetChanged()
        } else {
            val snackBar = Snackbar.make(movieList, message, Snackbar.LENGTH_LONG)
            val tv = snackBar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            tv.setTextColor(Color.WHITE)
            snackBar.show()
        }
        swipeLayout?.isRefreshing = false
    }

    override fun onToggleFavourite(pos: Int) {
        adapter.getObject(pos)
        adapter.notifyItemChanged(pos)
    }
}