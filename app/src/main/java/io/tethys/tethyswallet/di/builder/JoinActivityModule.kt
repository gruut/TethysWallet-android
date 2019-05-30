package io.tethys.tethyswallet.di.builder

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import io.tethys.tethyswallet.di.ViewModelKey
import io.tethys.tethyswallet.ui.join.JoinActivity
import io.tethys.tethyswallet.ui.join.JoinAutonymFragment
import io.tethys.tethyswallet.ui.join.JoinFragment
import io.tethys.tethyswallet.ui.join.JoinViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface JoinActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: JoinActivity): AppCompatActivity

    @ContributesAndroidInjector
    fun contributeJoinFragment(): JoinFragment

    @ContributesAndroidInjector
    fun contributeJoinAutonymFragment(): JoinAutonymFragment

    @Binds
    @IntoMap
    @ViewModelKey(JoinViewModel::class)
    fun bindJoinViewModel(
        joinViewModel: JoinViewModel
    ): ViewModel
}