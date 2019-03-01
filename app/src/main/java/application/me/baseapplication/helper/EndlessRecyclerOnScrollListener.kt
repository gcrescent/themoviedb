package application.me.baseapplication.helper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerOnScrollListener(private val mVisibleThreshold: Int = 1) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val manager = recyclerView!!.layoutManager as LinearLayoutManager
        if (manager.itemCount <= manager.findLastVisibleItemPosition() + mVisibleThreshold) {
            onLoadMore()
        }
    }

    abstract fun onLoadMore()
}