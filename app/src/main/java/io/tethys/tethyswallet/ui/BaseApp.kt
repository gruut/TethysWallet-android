package io.tethys.tethyswallet.ui

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.tethys.tethyswallet.BuildConfig
import io.tethys.tethyswallet.data.local.AppPreferenceHelper
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.di.component.DaggerAppComponent
import io.tethys.tethyswallet.di.module.NetworkModule
import io.tethys.tethyswallet.utils.AppConstants
import timber.log.Timber
import java.io.IOException
import java.net.SocketException

class BaseApp : DaggerApplication() {

    override fun onCreate() {
        prefHelper = AppPreferenceHelper(applicationContext, AppConstants.PREF_NAME)
        super.onCreate()

        setupTimber()
        setupErrorHandler()
    }

    companion object {
        lateinit var prefHelper: PreferenceHelper
            private set
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setupErrorHandler() {
        RxJavaPlugins.setErrorHandler { e ->
            var error = e
            if (error is UndeliverableException) {
                error = e.cause
            }
            if (error is IOException || error is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (error is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if (error is NullPointerException || error is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler
                    .uncaughtException(Thread.currentThread(), error)
                return@setErrorHandler
            }
            if (error is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler
                    .uncaughtException(Thread.currentThread(), error)
                return@setErrorHandler
            }
            Timber.w(error, "Undeliverable exception received, not sure what to do")
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .networkModule(NetworkModule.instance)
            .build()
    }
}