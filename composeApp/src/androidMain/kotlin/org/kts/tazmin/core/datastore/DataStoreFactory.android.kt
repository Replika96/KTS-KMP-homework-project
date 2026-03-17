package org.kts.tazmin.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

private lateinit var appContext: Context

fun initDataStore(context: Context) {
    appContext = context.applicationContext
}

private val dataStore: DataStore<Preferences> by lazy {
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            appContext.filesDir
                .resolve(DATA_STORE_FILE_NAME)
                .absolutePath
                .toPath()
        }
    )
}

actual fun createPlatformDataStore(): DataStore<Preferences> = dataStore
