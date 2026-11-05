package com.indteam.app

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            INDTEAMScreen()
        }
    }
}

@Composable
fun INDTEAMScreen() {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val deviceId = remember {
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "ID tidak tersedia"
    }

    var token by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("10") }
    var minimumPrice by remember { mutableStateOf("8000") }
    var maximumPrice by remember { mutableStateOf("200000") }
    var useMiles by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("Silakan aktivasi IND TEAM.") }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "IND TEAM",
                    style = MaterialTheme.typography.headlineLarge
                )

                Text("AKTIVASI APLIKASI")

                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Masukkan Unique Code") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("ID HANDPHONE")
                Text(
                    text = deviceId,
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(deviceId))
                        message = "ID handphone berhasil disalin."
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("KLIK ID HANDPHONE")
                }

                Button(
                    onClick = {
                        message = if (token.isBlank()) {
                            "Masukkan Unique Code terlebih dahulu."
                        } else {
                            "Server aktivasi belum dikonfigurasi."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("MASUK")
                }

                HorizontalDivider()

                Text(
                    text = "PENGATURAN ORDER",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = distance,
                    onValueChange = { distance = it },
                    label = {
                        Text(
                            if (useMiles) "Jarak maksimum (mil)"
                            else "Jarak maksimum (KM)"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Gunakan satuan mil")
                    Switch(
                        checked = useMiles,
                        onCheckedChange = { useMiles = it }
                    )
                }

                OutlinedTextField(
                    value = minimumPrice,
                    onValueChange = { minimumPrice = it },
                    label = { Text("Harga minimum (Rp)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maximumPrice,
                    onValueChange = { maximumPrice = it },
                    label = { Text("Harga maksimum (Rp)") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "IND TEAM membantu menyaring informasi order. " +
                        "Keputusan menerima order tetap dilakukan secara manual.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
