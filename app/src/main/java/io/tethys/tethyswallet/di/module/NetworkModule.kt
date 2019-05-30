package io.tethys.tethyswallet.di.module

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.tethys.tethyswallet.data.api.GAApi
import io.tethys.tethyswallet.di.OkHttpClientGA
import io.tethys.tethyswallet.di.RetrofitGA
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
open class NetworkModule {

    companion object {
        val instance = NetworkModule()
    }

    @Singleton
    @Provides
    fun provideJsonConverter(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @OkHttpClientGA
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @RetrofitGA
    @Singleton
    @Provides
    fun provideRetrofit(@OkHttpClientGA okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://ec2-13-209-161-44.ap-northeast-2.compute.amazonaws.com:48080/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Singleton
    @Provides
    fun provideGAApi(@RetrofitGA retrofit: Retrofit): GAApi {
        return retrofit.create(GAApi::class.java)
    }
}