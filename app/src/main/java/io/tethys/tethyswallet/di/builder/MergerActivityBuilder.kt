package io.tethys.tethyswallet.di.builder

import io.tethys.tethyswallet.ui.merger.MergerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MergerActivityBuilder {
    @ContributesAndroidInjector(modules = [MergerActivityModule::class])
    fun contributeMergerActivity(): MergerActivity
}