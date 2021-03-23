package c.m.koskosan

import android.app.Application
import c.m.koskosan.di.repositoryModule
import c.m.koskosan.di.viewModelModule
import c.m.koskosan.ui.form.add.user.profile.AddUserProfileViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber
import timber.log.Timber.DebugTree


class KosKosanApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
            Timber.plant(DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@KosKosanApplication)
            modules(listOf(viewModelModule, repositoryModule))
        }
    }
}