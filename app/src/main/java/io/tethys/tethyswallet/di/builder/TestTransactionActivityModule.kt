package io.tethys.tethyswallet.di.builder

import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import io.tethys.tethyswallet.ui.test_transaction.TestTransactionActivity

@Module
interface TestTransactionActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: TestTransactionActivity): AppCompatActivity
}