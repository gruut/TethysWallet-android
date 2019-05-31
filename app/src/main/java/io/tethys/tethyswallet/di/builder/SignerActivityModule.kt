package io.tethys.tethyswallet.di.builder

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import io.tethys.tethyswallet.di.ViewModelKey
import io.tethys.tethyswallet.ui.merger.MergerActivity
import io.tethys.tethyswallet.ui.merger.MergerFragment
import io.tethys.tethyswallet.ui.merger.MergerViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.tethys.tethyswallet.ui.signer.SignerActivity

@Module
interface SignerActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: SignerActivity): AppCompatActivity
}