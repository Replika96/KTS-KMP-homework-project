package org.kts.tazmin.feature.auth.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun LoginWebView(
    onCodeReceived: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
)