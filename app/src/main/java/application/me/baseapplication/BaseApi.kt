package application.me.baseapplication

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class BaseApi {

    companion object {

        lateinit var instance: BaseApi

        fun initiate() {
            instance = BaseApi()
        }

        fun registerService(clazz: Class<out Any>) {
            instance.registerService(clazz)
        }

        fun <T : Any> getService(clazz: Class<T>): T {
            return instance.getService(clazz)
        }
    }

    var retrofit: Retrofit
    private val services: HashMap<String, Any> = HashMap()

    init {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()

        val requestInterceptor = Interceptor { chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()

            //add header & query here

            val urlBuilder = request.url()
                .newBuilder()
                .addQueryParameter("api_key", "76ef7a63264b9ebde62535e647d71997")

            requestBuilder.url(urlBuilder.build())

            chain.proceed(requestBuilder.build())
        }

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val builder = OkHttpClient.Builder()

        // Request Interceptor
        builder.addInterceptor(requestInterceptor)

        // Logging Interceptor
        builder.addInterceptor(httpLoggingInterceptor)

        val client = builder.build()

        val url = "https://api.themoviedb.org/3/" //api url

        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

    }

    private fun registerService(clazz: Class<out Any>) {
        services[clazz.name] = retrofit.create(clazz)
    }

    private fun <T : Any> getService(clazz: Class<T>): T {
        return clazz.cast(services[clazz.name])
    }
}