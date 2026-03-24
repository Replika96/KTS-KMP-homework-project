package org.kts.tazmin.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.kts.tazmin.core.database.AppDatabase
import org.kts.tazmin.core.datastore.UserPreferences
import org.kts.tazmin.core.token.TokenStorage

// модуль для данных
val dataModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                androidContext().applicationContext.filesDir
                    .resolve(relative = DATA_STORE_FILE_NAME)
                    .absolutePath
                    .toPath()
            }
        )
    }

    single { UserPreferences(dataStore = get()) }
}

// модуль для безопасности
val securityModule = module {
    single { TokenStorage(androidContext().applicationContext) }
}

// модуль для базы данных
val databaseModule = module {
    single {
        Room.databaseBuilder(
            context = get(),
            klass = AppDatabase::class.java,
            name = "app_database"
        ).fallbackToDestructiveMigration(false)
            .build()
    }

    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().courseDao() }
    single { get<AppDatabase>().myCoursesDao() }
    single { get<AppDatabase>().catalogDao() }
}

const val DATA_STORE_FILE_NAME = "user.preferences_pb"
