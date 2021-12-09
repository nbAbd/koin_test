package com.pieaksoft.event.consumer.android.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.pieaksoft.event.consumer.android.BuildConfig
import com.pieaksoft.event.consumer.android.events.EventsRepo
import com.pieaksoft.event.consumer.android.events.EventsRepoImpl
import com.pieaksoft.event.consumer.android.events.EventsVM

import com.pieaksoft.event.consumer.android.network.OkHttpInterceptor
import com.pieaksoft.event.consumer.android.network.ServiceApi
import com.pieaksoft.event.consumer.android.ui.events_fragments.EventsCalculationVM
import com.pieaksoft.event.consumer.android.ui.login.LoginRepo
import com.pieaksoft.event.consumer.android.ui.login.LoginRepoImpl
import com.pieaksoft.event.consumer.android.ui.login.LoginVM
import com.pieaksoft.event.consumer.android.ui.profile.ProfileRepo
import com.pieaksoft.event.consumer.android.ui.profile.ProfileRepoImpl
import com.pieaksoft.event.consumer.android.ui.profile.ProfileVM
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_NAME
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val RetrofitModule = module {
    single {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    single { OkHttpInterceptor() }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .addInterceptor(get<OkHttpInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.Endpoint)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get<OkHttpClient>())
            .build()
    }

    single { get<Retrofit>().create(ServiceApi::class.java) }
}


val AppModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    single<LoginRepo> { LoginRepoImpl(get()) }
    single<ProfileRepo> { ProfileRepoImpl(get()) }
    single<EventsRepo> { EventsRepoImpl(get()) }
//    single { FavoritesRepository(get()) }
//    single { ChatRepository(get()) }
//    single { EventsRepository(get()) }
//    single { OrderRepository(get()) }
//    single { BasketRepository(get()) }
    viewModel { LoginVM(get(), get()) }
    viewModel { ProfileVM(get(), get()) }
    viewModel { EventsVM(get(), get()) }
    viewModel { EventsCalculationVM(get()) }
//    viewModel { RecommendedVM(get()) }
//    viewModel { StoreVM(get()) }
//    viewModel { ProfileVM(get()) }
//    viewModel { AuthVM(get()) }
//    viewModel { FirebaseVM() }
//    viewModel { FavoriteVM(get()) }
//    viewModel { ChatViewModel(get()) }
//    viewModel { EventStoreVM(get()) }
//    viewModel { OrderVM(get()) }
//    viewModel { SplashVM() }
//    viewModel { OrdersListVM(get()) }
//    viewModel { BasketVM(get()) }

}