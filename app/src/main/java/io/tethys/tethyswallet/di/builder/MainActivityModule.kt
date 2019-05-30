package io.tethys.tethyswallet.di.builder

import androidx.appcompat.app.AppCompatActivity
import io.tethys.tethyswallet.ui.main.MainActivity
import dagger.Binds
import dagger.Module

@Module
interface MainActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: MainActivity): AppCompatActivity
}