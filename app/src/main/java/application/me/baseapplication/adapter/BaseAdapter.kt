package application.me.baseapplication.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import application.me.baseapplication.R
import application.me.baseapplication.api.model.ObjectInterface
import application.me.baseapplication.helper.EndlessRecyclerOnScrollListener
import application.me.baseapplication.helper.ErrorHandler
import application.me.baseapplication.view.CustomDialog
import com.google.android.material.snackbar.Snackbar
import id.paprika.paprika.api.exception.ApiError
import kotlinx.android.synthetic.main.error_view.view.*
import retrofit2.Call

abstract class BaseRecyclerAdapter<T : ObjectInterface>(protected val context: Context) :
    RecyclerView.Adapter<BaseViewHolder>() {

    protected val VIEW_TYPE_PROGRESS = 1
    protected val VIEW_TYPE_EMPTY = 2
    protected val VIEW_TYPE_ERROR = 3
    protected val VIEW_TYPE_ITEM = 4

    private val objects: MutableList<T> = ArrayList()
    protected lateinit var recyclerView: RecyclerView
    protected var calls: MutableList<Call<*>> = ArrayList()

    private val queryLoadListeners: MutableList<OnQueryLoadListener<T>> = ArrayList()
    private var scrollListener: EndlessRecyclerOnScrollListener

    private var currentPage: Int = 0
    private var nextPage: Int = 0
    private var objectsPerPage: Int = 0
    private var paginationEnabled: Boolean = false
    private var hasNextPage: Boolean = false
    private var loading: Boolean = false
    private var firstLoad: Boolean = false
    private var error: Boolean = false
    var hideIfEmpty: Boolean = false
    private var errorMessage: String? = null
    private var itemClickListener: OnItemClickListener<T> = object : OnItemClickListener<T> {
        override fun onItemClick(t: T) {

        }

    }

    private var errorClickListener: OnErrorClickListener = object : OnErrorClickListener {
        override fun onErrorClick() {
            loadObjects()
        }
    }

    init {
        firstLoad = true
        currentPage = 1
        nextPage = 1
        objectsPerPage = 20
        paginationEnabled = false
        hasNextPage = false
        errorMessage = context.getString(R.string.something_went_wrong)
        scrollListener = object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                loadNextPage()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewType == VIEW_TYPE_ERROR) {
            return ErrorViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.error_view, parent, false)
            )
        } else if (viewType == VIEW_TYPE_EMPTY) {
            return EmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_view, parent, false)
            )
        } else if (viewType == VIEW_TYPE_PROGRESS) {
            return ProgressViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_progress, parent, false)
            )
        }
        return onCreateItemViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ErrorViewHolder) {
            val errorView = holder.itemView
            errorView.errorMessage.text = getErrorMessage()
            errorView.setOnClickListener { getErrorClickListener().onErrorClick() }
            onBindErrorViewHolder(errorView)
        } else if (holder is EmptyViewHolder) {
            onBindEmptyViewHolder(holder.itemView)
        } else if (holder is ProgressViewHolder) {
            // do nothing
        } else {
            onBindItemViewHolder(holder, getItem(position), position)
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

    fun setObjects(items: List<T>) {
        this.objects.clear()
        this.objects.addAll(items)
        notifyDataSetChanged()
    }

    fun setInfiniteLoadTo(recyclerView: RecyclerView?) {
        if (null != recyclerView) {
            recyclerView.addOnScrollListener(scrollListener)
            paginationEnabled = true
        }
    }

    fun addOnQueryLoadListener(queryLoadListener: OnQueryLoadListener<T>) {
        this.queryLoadListeners.add(queryLoadListener)
    }

    fun setObjectsPerPage(objectsPerPage: Int) {
        this.objectsPerPage = objectsPerPage
    }

    /**
     * Check if should show empty view
     * !isFirstLoad => don't show if no data added
     */
    fun shouldShowEmptyView(): Boolean {
        return objects.isEmpty() && !isFirstLoad() && !hideIfEmpty
    }

    fun shouldShowErrorView(): Boolean {
        return objects.isEmpty() && isError() && !hideIfEmpty
    }

    fun getItem(position: Int): T? {
        return if (position < objects.size) objects[position] else null
    }

    fun getObject(position: Int): T? {
        return if (getItemViewType(position) == getItemType(position)) {
            if (position - 1 < objects.size) objects[position - 1] else null
        } else null
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }

    fun setLoading(isLoading: Boolean) {
        this.loading = isLoading
    }

    fun isLoading(): Boolean {
        return loading
    }

    fun isError(): Boolean {
        return error
    }

    fun setError(error: Boolean) {
        this.error = error
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun setFirstLoad(firstLoad: Boolean) {
        this.firstLoad = firstLoad
    }

    fun isFirstLoad(): Boolean {
        return firstLoad
    }

    protected fun clear() {
        objects.clear()
        notifyDataSetChanged()
    }

    fun loadNextPage() {
        if (hasNextPage && !isLoading()) {
            loadObjects(nextPage)
        }
    }

    /**
     * reset all data then reload from server
     */
    fun loadObjects() {
        hasNextPage = false
        currentPage = 1
        nextPage = 1
        loadObjects(currentPage, true)
    }

    private fun loadObjects(page: Int) {
        loadObjects(page, false)
    }

    /**
     * Memanggil query interface untuk mengambil data dari server kemudian menotify adapter
     *
     * @param page  halaman
     * @param clear perlukah menghapus data sebelum menambahkan data yang didapat dari server
     */
    private fun loadObjects(page: Int, clear: Boolean) {
        setLoading(true)
        for (queryLoadListener in queryLoadListeners) {
            queryLoadListener.onLoading()
        }

        fetchData(page, objectsPerPage, object : OnQueryFinishListener<T> {

            override fun success(loadedObjects: List<T>?) {
                var objects = loadedObjects
                currentPage = page
                nextPage++
                setError(false)
                if (null == objects) {
                    objects = ArrayList()
                }
                hasNextPage = objects.size >= objectsPerPage
                if (clear) {
                    this@BaseRecyclerAdapter.objects.clear()
                }
                this@BaseRecyclerAdapter.objects.addAll(objects)
                notifyDataSetChanged()

                for (queryLoadListener in queryLoadListeners) {
                    queryLoadListener.onSuccess(objects)
                }
                setLoading(false)
                setFirstLoad(false)
            }

            override fun failed(error: ApiError) {
                setLoading(false)

                if (objects.isEmpty()) {
                    setError(true)
                    setErrorMessage(ErrorHandler.getErrorMessage(context, error))
                    notifyDataSetChanged()
                } else if (!hideIfEmpty) {
                    val dialog = CustomDialog(context, ErrorHandler.getErrorMessage(context, error))
                    dialog.show()
                }

                for (queryLoadListener in queryLoadListeners) {
                    queryLoadListener.onFailure(error, false)
                }
            }

            override fun failed(t: Throwable?, isCanceled: Boolean) {
                setLoading(false)
                if (objects.isEmpty()) {
                    setError(true)
                    setErrorMessage(ErrorHandler.getErrorMessage(context, t))
                    notifyDataSetChanged()
                } else if (!hideIfEmpty) {
                    val snackbar =
                        Snackbar.make(recyclerView, ErrorHandler.getErrorMessage(context, t), Snackbar.LENGTH_LONG)
                            .setAction(context.getString(R.string.retry), View.OnClickListener {
                                if (clear) {
                                    getErrorClickListener().onErrorClick()
                                } else {
                                    loadNextPage()
                                }
                            })
                            .setActionTextColor(ContextCompat.getColor(context, R.color.white))
                    val view = snackbar.getView()
                    val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                    tv.setTextColor(Color.WHITE)
                    snackbar.show()
                }

                for (queryLoadListener in queryLoadListeners) {
                    queryLoadListener.onFailure(t, isCanceled)
                }
            }

        })
    }

    /**
     * fetch data from server inside this method
     *
     * @param page     page to fetch
     * @param limit    how many objects per page
     * @param callback listener to know if the callback fail or success
     */
    abstract fun fetchData(page: Int, limit: Int, callback: OnQueryFinishListener<T>)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun getCurrentPage(): Int {
        return this.currentPage
    }

    private fun getPaginationCellRow(): Int {
        return objects.size
    }

    private fun shouldShowPaginationCell(): Boolean {
        return paginationEnabled && objects.size > 0 && hasNextPage
    }

    fun cancelCalls() {
        for (call in calls) {
            if (call.isExecuted && !call.isCanceled) {
                call.cancel()
            }
        }
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    abstract fun onBindItemViewHolder(baseViewHolder: BaseViewHolder, t: T?, position: Int)

    fun getItemClickListener(): OnItemClickListener<T> {
        return itemClickListener
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener<T>) {
        this.itemClickListener = itemClickListener
    }

    /**
     * Extend this if you need to have many item types
     */
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

    fun getErrorClickListener(): OnErrorClickListener {
        return errorClickListener
    }

    fun setErrorClickListener(errorClickListener: OnErrorClickListener) {
        this.errorClickListener = errorClickListener
    }

    interface OnErrorClickListener {
        fun onErrorClick()
    }

    interface OnQueryFinishListener<T : ObjectInterface> {
        fun success(objects: List<T>?)

        fun failed(error: ApiError)

        fun failed(t: Throwable?, isCanceled: Boolean)
    }

    interface OnQueryLoadListener<T : ObjectInterface> {
        fun onLoading()

        fun onSuccess(objects: List<T>?)

        fun onFailure(t: Throwable?, isCanceled: Boolean)
    }

    class ProgressViewHolder(view: View) : BaseViewHolder(view)

    class EmptyViewHolder(view: View) : BaseViewHolder(view)

    class ErrorViewHolder(view: View) : BaseViewHolder(view)
}