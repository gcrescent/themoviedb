package application.me.baseapplication.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setOnClickListener(onClickListener: View.OnClickListener?) {
        itemView.setOnClickListener(onClickListener)
    }
}