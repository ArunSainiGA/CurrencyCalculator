package com.asp.currencycalculator.di

import android.app.Application
import androidx.room.Room
import com.asp.currencycalculator.utils.AppConstant
import com.asp.data.repository.RepositoryImpl
import com.asp.data.datastore.CurrencyDataStore
import com.asp.data.local.CurrencyDB
import com.asp.data.local.datastore.CurrencyLocalStore
import com.asp.data.local.cache.CacheStrategy
import com.asp.data.local.cache.CurrencyCacheStrategy
import com.asp.data.local.dao.CurrencyDao
import com.asp.data.remote.datastore.CurrencyRemoteStore
import com.asp.data.remote.service.CurrencyService
import com.asp.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideRoomDatabase(application: Application): CurrencyDB {
        return Room.databaseBuilder(application, CurrencyDB::class.java, AppConstant.DB_NAME).build()
    }

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()

    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(currencyDB: CurrencyDB): CurrencyDao {
        return currencyDB.getCurrencyDao()
    }

    @Provides
    @Singleton
    fun provideCurrencyService(retrofit: Retrofit): CurrencyService {
        return retrofit.create(CurrencyService::class.java)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindsModule {
        @Binds
        @Singleton
        fun bindLocalDataStore(ds: CurrencyLocalStore): CurrencyDataStore.CurrencyLocalDataStore

        @Binds
        @Singleton
        fun bindRemoteDataStore(ds: CurrencyRemoteStore): CurrencyDataStore.CurrencyRemoteDataStore

        @Binds
        @Singleton
        fun bindRepository(repositoryImpl: RepositoryImpl): Repository

        @Binds
        @Singleton
        fun bindCacheStrategy(repositoryImpl: CurrencyCacheStrategy): CacheStrategy
    }
}