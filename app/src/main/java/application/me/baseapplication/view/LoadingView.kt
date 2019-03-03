package application.me.baseapplication.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import application.me.baseapplication.R
import kotlinx.android.synthetic.main.error_view.view.*
import kotlinx.android.synthetic.main.loading_view.view.*

class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    enum class State {
        LOADING, SUCCESS, ERROR
    }

    private var state: State = State.LOADING

    init {
        val v = View.inflate(getContext(), R.layout.loading_view, null);
        addView(v);

        loadingViewContainer.setOnClickListener {}
        errorView.setOnClickListener {}

        setViewState(State.LOADING)
    }

    private fun setViewState(state: State) {
        this.state = state;
        when (state) {
            State.LOADING -> {
                alpha = 1f
                visibility = View.VISIBLE
                loadingViewContainer.visibility = View.VISIBLE
                errorView.visibility = View.GONE
            }
            State.ERROR -> {
                alpha = 1f
                visibility = View.VISIBLE
                loadingViewContainer.visibility = View.GONE
                errorView.visibility = View.VISIBLE
            }
            else -> visibility = View.GONE
        }
    }

    fun setLoading() {
        setViewState(State.LOADING)
    }

    fun setSuccess() {
        setViewState(State.SUCCESS)
    }

    fun setError(message: String?) {
        errorMessage.text = message
        setViewState(State.ERROR)
    }

    fun setErrorClickListener(onClickListener: OnClickListener) {
        errorView.setOnClickListener(onClickListener)
    }
}