package org.kts.tazmin.feature.auth.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun LoginWebView(
    onCodeReceived: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        Text("iOS WebView будет реализован позже")
    }
}
