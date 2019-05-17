package com.veronnnetworks.veronnwallet.di.builder

import com.veronnnetworks.veronnwallet.ui.join.JoinActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface JoinActivityBuilder {
    @ContributesAndroidInjector(modules = [JoinActivityModule::class])
    fun contributeJoinActivity(): JoinActivity
}