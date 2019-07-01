package io.tethys.tethyswallet.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.tethys.tethyswallet.service.MessageSenderService

@Module
interface MessageSenderServiceBuilder {
    @ContributesAndroidInjector
    fun contributeMessageSenderService(): MessageSenderService
}