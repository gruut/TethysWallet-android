package com.veronnnetworks.veronnwallet.di.module

import android.app.Application
import android.content.Context
import com.veronnnetworks.veronnwallet.auth.AppKeyStoreHelper
import com.veronnnetworks.veronnwallet.auth.KeyStoreHelper
import com.veronnnetworks.veronnwallet.data.GADataRepository
import com.veronnnetworks.veronnwallet.data.GARepository
import com.veronnnetworks.veronnwallet.data.api.GAApi
import com.veronnnetworks.veronnwallet.data.local.AppPreferenceHelper
import com.veronnnetworks.veronnwallet.data.local.PreferenceHelper
import com.veronnnetworks.veronnwallet.di.PreferenceInfo
import com.veronnnetworks.veronnwallet.utils.AppConstants
import com.veronnnetworks.veronnwallet.utils.rx.AppSchedulerProvider
import com.veronnnetworks.veronnwallet.utils.rx.SchedulerProvider
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
    @PreferenceInfo
    fun providePreferenceName(): String {
        return AppConstants.PREF_NAME
    }

    @Provides
    @Singleton
    fun providePrefHelper(appPreferenceHelper: AppPreferenceHelper): PreferenceHelper {
        return appPreferenceHelper
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
        preferenceHelper: PreferenceHelper,
        schedulerProvider: SchedulerProvider
    ): KeyStoreHelper = AppKeyStoreHelper(preferenceHelper, schedulerProvider)

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }
}