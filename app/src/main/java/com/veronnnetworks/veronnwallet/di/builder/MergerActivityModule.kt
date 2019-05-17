package com.veronnnetworks.veronnwallet.di.builder

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.veronnnetworks.veronnwallet.di.ViewModelKey
import com.veronnnetworks.veronnwallet.ui.merger.MergerActivity
import com.veronnnetworks.veronnwallet.ui.merger.MergerFragment
import com.veronnnetworks.veronnwallet.ui.merger.MergerViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface MergerActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: MergerActivity): AppCompatActivity

    @ContributesAndroidInjector
    fun contributeJoinFragment(): MergerFragment

    @Binds
    @IntoMap
    @ViewModelKey(MergerViewModel::class)
    fun bindMergerViewModel(
        mergerViewModel: MergerViewModel
    ): ViewModel
}