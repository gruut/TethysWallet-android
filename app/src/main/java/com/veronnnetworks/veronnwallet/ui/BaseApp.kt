package com.veronnnetworks.veronnwallet.ui

import com.veronnnetworks.veronnwallet.BuildConfig
import com.veronnnetworks.veronnwallet.di.component.DaggerAppComponent
import com.veronnnetworks.veronnwallet.di.module.NetworkModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class BaseApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        setupTimber()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .networkModule(NetworkModule.instance)
            .build()
    }
}