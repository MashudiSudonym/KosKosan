package c.m.koskosan

import android.app.Application
import c.m.koskosan.di.databaseModule
import c.m.koskosan.di.repositoryModule
import c.m.koskosan.di.utilitiesModule
import c.m.koskosan.di.viewModelModule
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
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
            modules(listOf(utilitiesModule, viewModelModule, databaseModule, repositoryModule))
        }
    }
}