package com.veronnnetworks.veronnwallet.di.component

import android.app.Application
import com.veronnnetworks.veronnwallet.di.builder.JoinActivityBuilder
import com.veronnnetworks.veronnwallet.di.builder.MainActivityBuilder
import com.veronnnetworks.veronnwallet.di.builder.MergerActivityBuilder
import com.veronnnetworks.veronnwallet.di.builder.MergerServiceBuilder
import com.veronnnetworks.veronnwallet.di.module.AppModule
import com.veronnnetworks.veronnwallet.di.module.NetworkModule
import com.veronnnetworks.veronnwallet.di.module.ViewModelModule
import com.veronnnetworks.veronnwallet.ui.BaseApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        NetworkModule::class,
        ViewModelModule::class,
        JoinActivityBuilder::class,
        MainActivityBuilder::class,
        MergerActivityBuilder::class,
        MergerServiceBuilder::class
    ]
)
interface AppComponent : AndroidInjector<BaseApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun networkModule(networkModule: NetworkModule): Builder
        fun build(): AppComponent
    }

    override fun inject(instance: BaseApp)
}