package org.kts.tazmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import org.kts.tazmin.core.di.dataModule
import org.kts.tazmin.core.di.databaseModule
import org.kts.tazmin.core.di.securityModule
import org.kts.tazmin.core.presentation.App
import org.kts.tazmin.feature.auth.di.authModule
import org.kts.tazmin.feature.courses.di.coursesModule
import org.kts.tazmin.feature.profile.di.profileModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Napier.base(DebugAntilog())

        setContent {
            KoinApplication(
                application = {
                    androidContext(this@MainActivity)
                    modules(
                        databaseModule,
                        dataModule,
                        securityModule,
                        authModule,
                        coursesModule,
                        profileModule
                    )
                }
            ) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    KoinApplication(
        application = {
            //modules()
        }
    ) {
        App()
    }
}
