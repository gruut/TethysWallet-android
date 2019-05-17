package com.veronnnetworks.veronnwallet.di.module

import androidx.lifecycle.ViewModelProvider
import com.veronnnetworks.veronnwallet.di.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}