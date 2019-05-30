package io.tethys.tethyswallet.di.module

import android.app.Application
import android.content.Context
import io.tethys.tethyswallet.auth.AppKeyStoreHelper
import io.tethys.tethyswallet.auth.KeyStoreHelper
import io.tethys.tethyswallet.data.GADataRepository
import io.tethys.tethyswallet.data.GARepository
import io.tethys.tethyswallet.data.api.GAApi
import io.tethys.tethyswallet.data.local.AppPreferenceHelper
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.di.PreferenceInfo
import io.tethys.tethyswallet.utils.AppConstants
import io.tethys.tethyswallet.utils.rx.AppSchedulerProvider
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideGARepository(
        api: GAApi,
        schedulerProvider: SchedulerProvider
    ): GARepository = GADataRepository(api, schedulerProvider)

    @Provides
    @Singleton
    fun provideKeyStoreHelper(
        schedulerProvider: SchedulerProvider
    ): KeyStoreHelper = AppKeyStoreHelper(schedulerProvider)

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }
}