package io.tethys.tethyswallet.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.tethys.tethyswallet.ui.signer.SignerActivity

@Module
interface SignerActivityBuilder {
    @ContributesAndroidInjector(modules = [SignerActivityModule::class])
    fun contributeSignerActivity(): SignerActivity
}