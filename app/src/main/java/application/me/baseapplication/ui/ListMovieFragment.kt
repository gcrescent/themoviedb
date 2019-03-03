package application.me.baseapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import application.me.baseapplication.BaseFragment
import application.me.baseapplication.R
import application.me.baseapplication.adapter.BaseRecyclerAdapter
import application.me.baseapplication.adapter.MovieAdapter
import application.me.baseapplication.api.model.Movie
import application.me.baseapplication.helper.GridSpacingItemDecoration
import application.me.baseapplication.toPx
import kotlinx.android.synthetic.main.fragment_list_movie.*

class ListMovieFragment : BaseFragment() {

    companion object {

        const val POPULAR_MOVIES = "popular"
        const val TOP_RATED_MOVIE = "top_rated"
        const val FAVOURITE_MOVIE = "favourite"

        private const val INTENT_SORT_BY = "sortBy"

        fun newInstance(sortBy: String): Fragment {
            val fragment = ListMovieFragment()
            val args = Bundle()
            args.putString(INTENT_SORT_BY, sortBy)
            fragment.arguments = args
            return fragment
        }
    }

    private var adapter: MovieAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_movie, container, false);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            adapter = MovieAdapter(requireContext(), it.getString(INTENT_SORT_BY, POPULAR_MOVIES))
        } ?: kotlin.run {
            adapter = MovieAdapter(requireContext(), POPULAR_MOVIES)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter?.getItemViewType(position)) {
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

        swipeLayout.setOnRefreshListener {
            loadData()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        swipeLayout.isRefreshing = true
        adapter?.setInfiniteLoadTo(movieList)
        adapter?.setErrorClickListener(object : BaseRecyclerAdapter.OnErrorClickListener {
            override fun onErrorClick() {
                loadData()
            }
        })
        adapter?.setItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener<Movie> {
            override fun onItemClick(t: Movie) {
                activity?.let {
                    startActivity(MovieDetailActivity.newIntent(it, t))
                }
            }
        })
        adapter?.addOnQueryLoadListener(object : BaseRecyclerAdapter.OnQueryLoadListener<Movie> {
            override fun onLoading() {

            }

            override fun onSuccess(objects: List<Movie>?) {
                swipeLayout?.isRefreshing = false
            }

            override fun onFailure(t: Throwable?) {
                swipeLayout?.isRefreshing = false
            }
        })
        adapter?.loadObjects()
        movieList.adapter = adapter
    }
}