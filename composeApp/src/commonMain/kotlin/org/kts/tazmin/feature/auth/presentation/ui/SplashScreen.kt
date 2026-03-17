package org.kts.tazmin.feature.auth.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.stepik_logotype_blac
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.stepik_logotype_blac),
            contentDescription = null,
            modifier = Modifier.size(220.dp)
        )
    }
}