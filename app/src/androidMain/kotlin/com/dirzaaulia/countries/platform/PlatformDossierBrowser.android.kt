package com.dirzaaulia.countries.platform

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dirzaaulia.countries.ui.components.AdaptiveInfoSheet
import com.dirzaaulia.countries.ui.components.MinimalistCloseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformDossierBrowser(
    url: String,
    onClose: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val webView =
        remember {
            WebViewHolder()
        }
    DisposableEffect(Unit) {
        onDispose {
            webView.view?.apply {
                stopLoading()
                loadUrl("about:blank")
                destroy()
            }
            webView.view = null
        }
    }
    BackHandler {
        if (webView.view?.canGoBack() == true) webView.view?.goBack() else onClose()
    }
    AdaptiveInfoSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text("WEB PAGE", color = Color.White, modifier = Modifier.weight(1f))
            MinimalistCloseButton(onClick = onClose)
        }
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = false
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    webViewClient =
                        object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest,
                            ): Boolean = request.url.scheme != "https"
                        }
                    loadUrl(url)
                    webView.view = this
                }
            },
            modifier = Modifier.fillMaxWidth().height(600.dp),
        )
    }
}

private class WebViewHolder {
    var view: WebView? = null
}
