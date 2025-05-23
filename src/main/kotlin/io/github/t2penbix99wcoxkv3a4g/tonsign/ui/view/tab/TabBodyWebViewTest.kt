package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view.tab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.browser.WebView
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10n

class TabBodyWebViewTest : TabBodyBase() {
    override val title: String
        get() = "Web View Test"
    override val id: String
        get() = "web_view_test"
    override val enabled: Boolean
        get() = Utils.logger.isDebugEnabled()

    @Composable
    override fun icon() {
        Icon(Icons.Default.Favorite, contentDescription = title.l10n())
    }

    @Composable
    override fun view(navController: NavHostController, padding: PaddingValues) {
        val mUrl = "https://google.com"
        val mHtml = "<html><body><h1>Hello, World!</h1></body></html>"
        val mTitle = "Google"
        val mHeaders = mapOf("Content-Type" to "text/html; charset=UTF-8")
        val mUserAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36 Edg/100.0.1185.50"
        
        WebView("https://mempool.space/")
    }
}