package io.tethys.tethyswallet.di.builder

import io.tethys.tethyswallet.service.MergerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MergerServiceBuilder {
    @ContributesAndroidInjector
    fun contributeMergerService(): MergerService
}