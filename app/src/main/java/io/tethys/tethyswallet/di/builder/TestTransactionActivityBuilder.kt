package io.tethys.tethyswallet.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.tethys.tethyswallet.ui.test_transaction.TestTransactionActivity

@Module
interface TestTransactionActivityBuilder {
    @ContributesAndroidInjector(modules = [TestTransactionActivityModule::class])
    fun contributeTestTransactionActivity(): TestTransactionActivity
}