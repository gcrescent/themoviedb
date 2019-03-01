package application.me.baseapplication.helper

import android.content.Context
import application.me.baseapplication.R
import id.paprika.paprika.api.exception.ApiError
import java.io.IOException

class ErrorHandler {

    companion object {

        fun getErrorMessage(context: Context, e: ApiError): String? {
            e.message?.let {
                return it
            }

            return context.getString(R.string.something_went_wrong)
        }

        fun getErrorMessage(context: Context?, t: Throwable?): String {
            return if (context != null) {
                if (t is IOException) {
                    context.getString(R.string.network_error)
                } else {
                    context.getString(R.string.something_went_wrong)
                }
            } else ""
        }
    }
}