package org.kts.tazmin.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import org.koin.dsl.module
import org.kts.tazmin.core.database.AppDatabase
import org.kts.tazmin.core.datastore.createPlatformDataStore
import org.kts.tazmin.feature.courses.data.local.CourseDao
import org.kts.tazmin.feature.profile.data.local.UserDao

val androidModule = module {
    single<DataStore<Preferences>> {
        createPlatformDataStore()
    }
    single {
        Room.databaseBuilder(
            context = get(),
            klass = AppDatabase::class.java,
            name = "app_database"
        ).fallbackToDestructiveMigration(false)
            .build()
    }

    single<UserDao> {
        val database = get<AppDatabase>()
        database.userDao()
    }

    single<CourseDao> {
        val database = get<AppDatabase>()
        database.courseDao()
    }
}
