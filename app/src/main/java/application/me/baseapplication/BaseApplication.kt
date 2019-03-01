package application.me.baseapplication

import android.app.Application

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BaseApi.initiate()

        // Register services here
    }
}