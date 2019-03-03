package application.me.baseapplication

import android.content.Context
import id.paprika.paprika.api.exception.ApiError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseCallback<T>(val context: Context) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess(call, response, response.body())
        } else {
            var error: ApiError? = null
            response.errorBody()?.let { errorBody ->
                val converter =
                    BaseApi.instance.retrofit.responseBodyConverter<ApiError>(ApiError::class.java, arrayOfNulls(0))

                try {
                    error = converter.convert(errorBody)
                } catch (e: Exception) {

                }
            }

            if (error == null) {
                error = ApiError(response.code(), context.getString(R.string.something_went_wrong))
            }

            onFailure(call, error!!)
        }
    }

    abstract fun onSuccess(call: Call<T>, response: Response<T>, t: T?)

    abstract fun onFailure(call: Call<T>, error: ApiError)
}