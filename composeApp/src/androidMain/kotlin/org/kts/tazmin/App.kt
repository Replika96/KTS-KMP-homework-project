package org.kts.tazmin

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.kts.tazmin.core.di.dataModule
import org.kts.tazmin.core.di.databaseModule
import org.kts.tazmin.core.di.securityModule
import org.kts.tazmin.feature.auth.di.authModule
import org.kts.tazmin.feature.catalog.di.coursesModule
import org.kts.tazmin.feature.course_details.di.courseDetailModule
import org.kts.tazmin.feature.course_reviews.di.reviewModule
import org.kts.tazmin.feature.profile.di.profileModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                databaseModule,
                dataModule,
                securityModule,
                authModule,
                coursesModule,
                profileModule,
                courseDetailModule,
                reviewModule
            )
        }
    }
}

