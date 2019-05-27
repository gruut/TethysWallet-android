package com.veronnnetworks.veronnwallet.di.builder

import com.veronnnetworks.veronnwallet.service.MergerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MergerServiceBuilder {
    @ContributesAndroidInjector
    fun contributeMergerService(): MergerService
}