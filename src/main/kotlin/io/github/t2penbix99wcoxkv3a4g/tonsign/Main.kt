package io.github.t2penbix99wcoxkv3a4g.tonsign

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

@Composable
@Preview
fun app() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Box(
            Modifier.fillMaxSize()
                .background(Color.DarkGray)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        OSCSender.send(true)
                    }
                ) {
                    Text("Force send true")
                }
                Button(
                    onClick = {
                        OSCSender.send(false)
                    }
                ) {
                    Text("Force send false")
                }
                Box(
                    Modifier.fillMaxSize()
                        .padding(end = 12.dp, bottom = 12.dp)
                ) {
                    textBox(text)
                }
            }
        }
    }
}

@Composable
fun textBox(text: String = "Item") {
    Box(
        Modifier.height(32.dp)
            .width(400.dp)
            .background(Color(200, 0, 0, 20))
            .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}

val reader = LogReader.Default


fun main() = application {
    scope.launch {
        reader.monitorRoundType()
    }

    Window(onCloseRequest = ::exitApplication, title = Utils.TITLE) {
        app()
    }
}
