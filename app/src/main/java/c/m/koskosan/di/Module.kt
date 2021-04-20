package c.m.koskosan.di

import androidx.room.Room
import c.m.koskosan.data.repository.ApplicationRepository
import c.m.koskosan.data.repository.AuthRepository
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.data.repository.LocalRepository
import c.m.koskosan.data.room.LocationDatabase
import c.m.koskosan.ui.detail.DetailViewModel
import c.m.koskosan.ui.form.add.user.profile.AddUserProfileViewModel
import c.m.koskosan.ui.form.order.OrderViewModel
import c.m.koskosan.ui.form.update.user.profile.UpdateUserProfileViewModel
import c.m.koskosan.ui.home.HomeViewModel
import c.m.koskosan.ui.main.MainViewModel
import c.m.koskosan.ui.maps.MapsViewModel
import c.m.koskosan.ui.profile.ProfileViewModel
import c.m.koskosan.ui.search.SearchViewModel
import c.m.koskosan.ui.splash.SplashscreenViewModel
import c.m.koskosan.ui.transaction.list.TransactionViewModel
import c.m.koskosan.util.ContextProviders
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val utilitiesModule: Module = module {
    single { ContextProviders() }
}
val viewModelModule: Module = module {
    viewModel { SplashscreenViewModel(get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { AddUserProfileViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { UpdateUserProfileViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { MapsViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { TransactionViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { OrderViewModel(get(), get()) }
}

val databaseModule: Module = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            LocationDatabase::class.java, "KosKosan Database"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<LocationDatabase>().locationDao() }
}

val repositoryModule: Module = module {
    single { AuthRepository() }
    single { FirebaseRepository() }
    single { LocalRepository(get()) }
    single { ApplicationRepository(get(), get()) }
}