package org.kts.tazmin.feature.auth.presentation.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.kts.tazmin.core.common.Config

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun LoginWebView(
    onCodeReceived: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url.toString()

                        // перехватываем редирект с code
                        if (url.startsWith(Config.STEPIK_REDIRECT_URI)) {
                            val code = request?.url?.getQueryParameter("code")
                            val error = request?.url?.getQueryParameter("error")

                            when {
                                code != null -> onCodeReceived(code)
                                error != null -> onError("Auth failed: $error")
                            }
                            return true
                        }
                        return false
                    }
                }

                // строим URL авторизации
                val authUrl = buildAuthUrl()
                loadUrl(authUrl)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

private fun buildAuthUrl(): String {
    return Uri.Builder()
        .scheme("https")
        .authority("stepik.org")
        .path("/oauth2/authorize/")
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("client_id", Config.STEPIK_CLIENT_ID)
        .appendQueryParameter("redirect_uri", Config.STEPIK_REDIRECT_URI)
        .appendQueryParameter("scope", "read write")
        .build()
        .toString()
}
