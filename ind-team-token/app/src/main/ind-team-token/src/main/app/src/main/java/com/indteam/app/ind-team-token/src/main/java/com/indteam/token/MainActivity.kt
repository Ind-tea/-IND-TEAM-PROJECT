package com.indteam.token

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TokenScreen()
        }
    }
}

@Composable
fun TokenScreen() {
    var duration by remember { mutableStateOf("1") }
    var message by remember {
        mutableStateOf("Server penerbitan token belum dihubungkan.")
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "IND TEAM TOKEN",
                style = MaterialTheme.typography.headlineMedium
            )

            Text("PENERBITAN UNIQUE CODE")

            Text("Pilih masa berlaku token:")

            listOf("1", "2", "3", "4", "5", "6").forEach {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = duration == it,
                        onClick = { duration = it }
                    )
                    Text("$it bulan")
                }
            }

            Button(
                onClick = {
                    message = "Penerbitan belum aktif. Server keamanan harus dipasang terlebih dahulu."
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("BUAT UNIQUE CODE")
            }

            HorizontalDivider()

            Text(message)
        }
    }
}
