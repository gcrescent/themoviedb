package application.me.baseapplication.view

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import application.me.baseapplication.R
import application.me.baseapplication.helper.DimensionHelper
import kotlinx.android.synthetic.main.dialog_custom.*

class CustomDialog(context: Context, message: CharSequence?) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)))
        setContentView(R.layout.dialog_custom)

        window?.setLayout(DimensionHelper.getScreenWidth(getContext()) * 6 / 7, WindowManager.LayoutParams.WRAP_CONTENT)

        setMessage(message)
    }

    fun setMessage(message: CharSequence?) {
        custom_dialog_message.text = message
    }

    fun setPositiveButton(text: String, listener: View.OnClickListener) {
        custom_dialog_single_button_container.visibility = View.GONE
        custom_dialog_double_button_container.visibility = View.VISIBLE
        custom_dialog_positive_button.text = text
        custom_dialog_positive_button.setOnClickListener {
            dismiss()
            listener.onClick(it)
        }
    }

    fun setNegativeButton(text: String, listener: View.OnClickListener) {
        custom_dialog_single_button_container.visibility = View.GONE
        custom_dialog_double_button_container.visibility = View.VISIBLE
        custom_dialog_negative_button.text = text
        custom_dialog_negative_button.setOnClickListener {
            dismiss()
            listener.onClick(it)
        }
    }

    fun setSingleButton(text: String, listener: View.OnClickListener) {
        custom_dialog_single_button_container.visibility = View.VISIBLE
        custom_dialog_double_button_container.visibility = View.GONE
        custom_dialog_single_button.setText(text)
        custom_dialog_single_button.setOnClickListener {
            dismiss()
            listener.onClick(it)
        }
    }
}