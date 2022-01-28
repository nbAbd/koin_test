package com.pieaksoft.event.consumer.android.di

import android.content.Context
import androidx.room.Room
import com.pieaksoft.event.consumer.android.db.AppDataBase
import com.pieaksoft.event.consumer.android.events.EventsRepository
import com.pieaksoft.event.consumer.android.events.EventsRepositoryImpl
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.ui.events_fragments.EventsCalculationViewModel
import com.pieaksoft.event.consumer.android.ui.activities.login.LoginRepo
import com.pieaksoft.event.consumer.android.ui.activities.login.LoginRepoImpl
import com.pieaksoft.event.consumer.android.ui.activities.login.LoginViewModel
import com.pieaksoft.event.consumer.android.ui.profile.ProfileRepo
import com.pieaksoft.event.consumer.android.ui.profile.ProfileRepoImpl
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DEFAULT_DB_NAME = "ELD_DB"
val AppModule = module {
    single { androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            DEFAULT_DB_NAME
        ).build()
    }
    single<LoginRepo> { LoginRepoImpl(get()) }
    single<ProfileRepo> { ProfileRepoImpl(get()) }
    single<EventsRepository> { EventsRepositoryImpl(get(), get()) }

    viewModel { LoginViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { EventViewModel(get(), get()) }
    viewModel { EventsCalculationViewModel(get()) }
}