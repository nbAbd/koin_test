package com.pieaksoft.event.consumer.android.di

import android.content.Context
import android.content.SharedPreferences

//val RetrofitModule = module {
//    single {
//        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
//    }
//
//    single { OkHttpInterceptor() }
//
//    single {
//        OkHttpClient.Builder()
//            .addInterceptor(get<HttpLoggingInterceptor>())
//            .addInterceptor(get<OkHttpInterceptor>())
//            .build()
//    }
//
//    single {
//        Retrofit.Builder()
//            .baseUrl(BuildConfig.Endpoint)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(get<OkHttpClient>())
//            .build()
//    }
//
//    single { get<Retrofit>().create(ServiceApi::class.java) }
//}
//
//val AppModule = module {
//    single<SharedPreferences> {
//        androidContext().getSharedPreferences(
//            SHARED_PREFERENCES_NAME,
//            Context.MODE_PRIVATE
//        )
//    }
//
//    single { HomeRepository(get()) }
//    single { FavoritesRepository(get()) }
//    single { ChatRepository(get()) }
//    single { EventsRepository(get()) }
//    single { OrderRepository(get()) }
//    single { BasketRepository(get()) }
//    viewModel { HomeVM(get()) }
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

//}