package org.kts.tazmin.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.*

actual fun httpClientEngine(): HttpClientEngine = Android.create()