package application.me.baseapplication.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import application.me.baseapplication.R
import application.me.baseapplication.ui.ListMovieFragment

class HomePagerAdapter(private val context: Context, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ListMovieFragment.newInstance(ListMovieFragment.POPULAR_MOVIES)
            1 -> ListMovieFragment.newInstance(ListMovieFragment.TOP_RATED_MOVIE)
            2 -> ListMovieFragment.newInstance(ListMovieFragment.FAVOURITE_MOVIE)
            else -> ListMovieFragment.newInstance(ListMovieFragment.POPULAR_MOVIES)
        }
    }

    override fun getCount(): Int {
        return 3;
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.tab_popular)
            1 -> context.getString(R.string.tab_top_rated)
            2 -> context.getString(R.string.tab_favourite)
            else -> context.getString(R.string.tab_popular)
        }
    }
}