package io.tethys.tethyswallet.di.component

import android.app.Application
import io.tethys.tethyswallet.di.module.AppModule
import io.tethys.tethyswallet.di.module.NetworkModule
import io.tethys.tethyswallet.di.module.ViewModelModule
import io.tethys.tethyswallet.ui.BaseApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.tethys.tethyswallet.di.builder.*
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
        SignerActivityBuilder::class,
        MergerServiceBuilder::class,
        MessageSenderServiceBuilder::class,
        TestTransactionActivityBuilder::class
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