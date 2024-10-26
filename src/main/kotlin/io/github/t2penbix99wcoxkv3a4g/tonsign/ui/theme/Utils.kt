@file:Suppress("FunctionName")

package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.theme

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

@Composable
fun MaterialEXTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors(background = md_theme_dark_background) else lightColors(
            background = md_theme_light_background
        )
    ) {
        val contextMenuRepresentation = if (isSystemInDarkTheme())
            DarkDefaultContextMenuRepresentation
        else
            LightDefaultContextMenuRepresentation
        CompositionLocalProvider(LocalContextMenuRepresentation provides contextMenuRepresentation) {
            Surface(Modifier.fillMaxSize(), content = content)
        }
    }
}