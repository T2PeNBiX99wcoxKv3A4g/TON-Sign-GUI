package io.github.t2penbix99wcoxkv3a4g.tonsign.browser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun WebView(
    startURL: String
) {
    // TODO: If it's html content we should save it locally...
    val cefBrowser = ComposeCef.rememberCEFBrowser(startURL)

    SwingPanel(
        background = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                cefBrowser.uiComponent.setSize(
                    layoutCoordinates.size.width,
                    layoutCoordinates.size.height
                )
            },
        factory = {
            cefBrowser.uiComponent
        }
    )
}