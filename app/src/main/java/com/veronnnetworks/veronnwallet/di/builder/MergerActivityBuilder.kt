package com.veronnnetworks.veronnwallet.di.builder

import com.veronnnetworks.veronnwallet.ui.merger.MergerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MergerActivityBuilder {
    @ContributesAndroidInjector(modules = [MergerActivityModule::class])
    fun contributeMergerActivity(): MergerActivity
}