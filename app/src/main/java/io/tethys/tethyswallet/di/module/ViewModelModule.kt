package io.tethys.tethyswallet.di.module

import androidx.lifecycle.ViewModelProvider
import io.tethys.tethyswallet.di.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}