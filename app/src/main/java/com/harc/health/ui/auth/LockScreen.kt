package com.harc.health.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.harc.health.R
import com.harc.health.logic.SessionManager
import com.harc.health.repository.LocalRepository
import kotlinx.coroutines.launch

@Composable
fun LockScreen(onAuthenticated: () -> Unit) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val repository = remember { LocalRepository(context) }
    val scope = rememberCoroutineScope()
    
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.lock_reset_title)) },
            text = { Text(stringResource(R.string.lock_reset_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository.clearAllData()
                            sessionManager.resetSession()
                            onAuthenticated() 
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.lock_reset_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "App Locked",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Enter your PIN to continue",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { 
                if (it.length <= 6) {
                    pin = it
                    error = null
                    
                    if (it.length >= 4) {
                        if (sessionManager.verifyPin(it)) {
                            onAuthenticated()
                        } else if (it.length == 6) {
                            error = context.getString(R.string.lock_wrong_pin)
                        }
                    }
                }
            },
            label = { Text("PIN") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        if (pin.length in 4..6 && !sessionManager.verifyPin(pin)) {
            Button(
                onClick = { 
                    if (sessionManager.verifyPin(pin)) {
                        onAuthenticated()
                    } else {
                        error = context.getString(R.string.lock_wrong_pin)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Unlock")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        TextButton(onClick = { showResetDialog = true }) {
            Text(stringResource(R.string.lock_forgot_pin), color = MaterialTheme.colorScheme.error)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Your data is stored locally and encrypted.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
