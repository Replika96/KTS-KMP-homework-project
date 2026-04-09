package org.kts.tazmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.compose.KoinApplication
import org.kts.tazmin.core.common.AndroidCrashLogger
import org.kts.tazmin.core.common.CrashlyticsAntilog
import org.kts.tazmin.core.presentation.AppRoot


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Napier.base(CrashlyticsAntilog(AndroidCrashLogger()))
        Napier.base(DebugAntilog())

        setContent {
            AppRoot()
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
        AppRoot()
    }
}
