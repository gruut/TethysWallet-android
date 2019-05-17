package com.veronnnetworks.veronnwallet.di.builder

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.veronnnetworks.veronnwallet.di.ViewModelKey
import com.veronnnetworks.veronnwallet.ui.join.JoinActivity
import com.veronnnetworks.veronnwallet.ui.join.JoinAutonymFragment
import com.veronnnetworks.veronnwallet.ui.join.JoinFragment
import com.veronnnetworks.veronnwallet.ui.join.JoinViewModel
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