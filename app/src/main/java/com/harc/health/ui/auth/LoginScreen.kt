package com.harc.health.ui.auth

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.harc.health.R
import com.harc.health.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val mainViewModel: MainViewModel = viewModel()

    val monthlyAlcohol by mainViewModel.monthlyAlcohol.collectAsState()
    val monthlyCigarettes by mainViewModel.monthlyCigarettes.collectAsState()
    val scrollState = rememberScrollState()

    // Professional Validation Logic
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6
    val canSubmit = email.isNotBlank() && password.isNotBlank() && !isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Branding Element
        Surface(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = stringResource(R.string.app_name).split(" ").firstOrNull() ?: "HARC",
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isSignUp) stringResource(R.string.login_create_account) else stringResource(R.string.login_welcome_back),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isSignUp) stringResource(R.string.login_sign_up_prompt) else stringResource(R.string.login_sign_in_prompt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Contextual Tracker Card (Expert Design)
        if (!isSignUp) {
            MonthlyTrackerCard(monthlyAlcohol, monthlyCigarettes)
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Email Field with Validation Feedback
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; error = null },
            label = { Text(stringResource(R.string.login_email_label)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = email.isNotBlank() && !isEmailValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = { Text(stringResource(R.string.login_password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = password.isNotBlank() && !isPasswordValid,
            supportingText = { if (isSignUp && password.isNotBlank() && !isPasswordValid) Text("Password must be at least 6 characters") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Action Button
        Button(
            onClick = {
                focusManager.clearFocus()
                if (isEmailValid && isPasswordValid) {
                    isLoading = true
                    val task = if (isSignUp) {
                        auth.createUserWithEmailAndPassword(email.trim(), password)
                    } else {
                        auth.signInWithEmailAndPassword(email.trim(), password)
                    }
                    
                    task.addOnSuccessListener {
                        isLoading = false
                        onLoginSuccess()
                    }.addOnFailureListener {
                        isLoading = false
                        error = it.localizedMessage ?: "Authentication failed"
                    }
                } else {
                    error = "Please provide a valid email and 6-character password"
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = canSubmit
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (isSignUp) stringResource(R.string.login_create_account).uppercase() else stringResource(R.string.login_sign_in).uppercase(),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            Text(
                text = " " + stringResource(R.string.profile_security_privacy).uppercase() + " ",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Modern Google SSO (Expert Integration)
        OutlinedButton(
            onClick = {
                scope.launch {
                    try {
                        isLoading = true
                        error = null
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId("119651407923-4e5a838i6kku18di1cpp0kb9s9mko6mu.apps.googleusercontent.com")
                            .setAutoSelectEnabled(true) // Professional: tries to auto-select if one account
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(context, request)
                        val credential = result.credential

                        if (credential is GoogleIdTokenCredential) {
                            val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                            auth.signInWithCredential(firebaseCredential).await()
                            onLoginSuccess()
                        }
                    } catch (e: GetCredentialException) {
                        Log.e("LoginScreen", "Google Sign-In Error", e)
                        // Don't show error if user just cancelled
                        if (e !is GetCredentialCancellationException) {
                            error = "Google Sign-In failed: ${e.message}"
                        }
                    } catch (e: Exception) {
                        error = "An unexpected error occurred"
                        Log.e("LoginScreen", "General Error", e)
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Icon(Icons.Default.Adjust, contentDescription = null, modifier = Modifier.size(18.dp)) // Minimalist placeholder for Google logo
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.login_continue_google), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { isSignUp = !isSignUp; error = null }) {
            Text(
                text = if (isSignUp) stringResource(R.string.login_already_have_account) else stringResource(R.string.login_dont_have_account),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MonthlyTrackerCard(alcohol: Int, cigarettes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.profile_biometric_login).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrackerItem(
                    label = "Alcohol",
                    value = "$alcohol units",
                    icon = Icons.Default.WineBar,
                    color = Color(0xFFE91E63)
                )
                TrackerItem(
                    label = "Cigarettes",
                    value = "$cigarettes count",
                    icon = Icons.Default.SmokingRooms,
                    color = Color(0xFF795548)
                )
            }
        }
    }
}

@Composable
fun TrackerItem(label: String, value: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}
