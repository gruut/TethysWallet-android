package com.veronnnetworks.veronnwallet.di.builder

import androidx.appcompat.app.AppCompatActivity
import com.veronnnetworks.veronnwallet.ui.main.MainActivity
import dagger.Binds
import dagger.Module

@Module
interface MainActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: MainActivity): AppCompatActivity
}