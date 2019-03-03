package application.me.baseapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import application.me.baseapplication.R
import application.me.baseapplication.api.model.ObjectInterface
import kotlinx.android.synthetic.main.error_view.view.*

abstract class BaseRecyclerAdapter<T : ObjectInterface>(protected val context: Context) :
    RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val VIEW_TYPE_PROGRESS = 1
        const val VIEW_TYPE_EMPTY = 2
        const val VIEW_TYPE_ERROR = 3
        const val VIEW_TYPE_ITEM = 4
    }

    private val objects: MutableList<T> = ArrayList()

    var currentPage: Int = 0
    var objectsPerPage: Int = 0
    private var paginationEnabled: Boolean = false
    var hasNextPage: Boolean = false
    var loading: Boolean = false
    private var firstLoad: Boolean = false
    var error: Boolean = false
    var errorMessage: String? = null
    private var itemClickListener: OnItemClickListener<T> = object : OnItemClickListener<T> {
        override fun onItemClick(t: T) {
        }
    }

    private var errorClickListener: OnErrorClickListener = object : OnErrorClickListener {
        override fun onErrorClick() {
        }
    }

    init {
        firstLoad = true
        currentPage = 1
        objectsPerPage = 20
        paginationEnabled = false
        hasNextPage = false
        errorMessage = context.getString(R.string.something_went_wrong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_ERROR -> ErrorViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.error_view, parent, false)
            )
            VIEW_TYPE_EMPTY -> EmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_view, parent, false)
            )
            VIEW_TYPE_PROGRESS -> ProgressViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_progress, parent, false)
            )
            else -> onCreateItemViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is ErrorViewHolder -> {
                val errorView = holder.itemView
                errorView.errorMessage.text = errorMessage
                errorView.setOnClickListener { getErrorClickListener().onErrorClick() }
                onBindErrorViewHolder(errorView)
            }
            is EmptyViewHolder -> onBindEmptyViewHolder(holder.itemView)
            is ProgressViewHolder -> {
                // do nothing
            }
            else -> onBindItemViewHolder(holder, getObject(position), position)
        }
    }

    override fun getItemCount(): Int {
        var count = objects.size
        val firstItemPosition = 0

        if (shouldShowErrorView()) {
            return firstItemPosition + 1
        }

        if (objects.isEmpty() && shouldShowEmptyView()) {
            return firstItemPosition + 1
        }

        if (shouldShowPaginationCell()) {
            count++
        }

        return count
    }

    override fun getItemViewType(position: Int): Int {
        val firstItemPosition = 0

        if (position == firstItemPosition && shouldShowErrorView()) {
            return VIEW_TYPE_ERROR
        }

        if (position == firstItemPosition && shouldShowEmptyView()) {
            return VIEW_TYPE_EMPTY
        }

        if (shouldShowPaginationCell() && position == getPaginationCellRow()) {
            return VIEW_TYPE_PROGRESS
        }

        return getItemType(position)
    }

    /**
     * Override this if you have several item type.
     * Can be use as custom view
     */
    protected open fun getItemType(position: Int): Int {
        return VIEW_TYPE_ITEM
    }

    fun getObjects(): List<T> {
        return objects
    }

    fun setObjects(items: List<T>?) {
        this.objects.clear()
        addObjects(items)
    }

    fun addObjects(items: List<T>?) {
        firstLoad = false
        error = false
        items?.let {
            this.objects.addAll(it)
        }
        hasNextPage = items?.size ?: 0 >= objectsPerPage && paginationEnabled
        notifyDataSetChanged()
        loading = false
        currentPage++
    }

    fun setPaginationEnabled(paginationEnabled: Boolean) {
        this.paginationEnabled = paginationEnabled
    }

    /**
     * Check if should show empty view
     * !isFirstLoad => don't show if no data added
     */
    private fun shouldShowEmptyView(): Boolean {
        return objects.isEmpty() && !firstLoad
    }

    private fun shouldShowErrorView(): Boolean {
        return objects.isEmpty() && error
    }

    fun getObject(position: Int): T? {
        return if (position < objects.size) objects[position] else null
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }

    protected fun clear() {
        objects.clear()
        notifyDataSetChanged()
    }

    private fun getPaginationCellRow(): Int {
        return objects.size
    }

    private fun shouldShowPaginationCell(): Boolean {
        return paginationEnabled && objects.size > 0 && hasNextPage
    }

    fun getItemClickListener(): OnItemClickListener<T> {
        return itemClickListener
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener<T>) {
        this.itemClickListener = itemClickListener
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    abstract fun onBindItemViewHolder(baseViewHolder: BaseViewHolder, t: T?, position: Int)

    interface OnItemClickListener<in T : ObjectInterface> {
        fun onItemClick(t: T)
    }

    /**
     * use to customize ErrorView
     */
    protected abstract fun onBindErrorViewHolder(errorView: View)

    /**
     * use to customize EmptyView
     */
    protected abstract fun onBindEmptyViewHolder(emptyView: View)

    private fun getErrorClickListener(): OnErrorClickListener {
        return errorClickListener
    }

    fun setErrorClickListener(errorClickListener: OnErrorClickListener) {
        this.errorClickListener = errorClickListener
    }

    interface OnErrorClickListener {
        fun onErrorClick()
    }

    class ProgressViewHolder(view: View) : BaseViewHolder(view)

    class EmptyViewHolder(view: View) : BaseViewHolder(view)

    class ErrorViewHolder(view: View) : BaseViewHolder(view)
}