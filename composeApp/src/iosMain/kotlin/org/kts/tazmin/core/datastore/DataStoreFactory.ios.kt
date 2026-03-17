package org.kts.tazmin.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


@OptIn(ExperimentalForeignApi::class)
actual fun createPlatformDataStore(): DataStore<Preferences> {
    return createDataStore {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        val path = requireNotNull(documentDirectory).path
        "$path/$DATA_STORE_FILE_NAME"
    }
}