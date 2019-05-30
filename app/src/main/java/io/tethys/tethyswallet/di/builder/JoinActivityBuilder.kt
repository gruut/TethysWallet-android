package io.tethys.tethyswallet.di.builder

import io.tethys.tethyswallet.ui.join.JoinActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface JoinActivityBuilder {
    @ContributesAndroidInjector(modules = [JoinActivityModule::class])
    fun contributeJoinActivity(): JoinActivity
}