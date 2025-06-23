package com.example.rushbuy.feature.auth.presentation.login.ui

import androidx.compose.foundation.Image
import com.example.rushbuy.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import com.example.rushbuy.feature.auth.domain.model.UserRole
import com.example.rushbuy.feature.auth.presentation.login.viewmodel.EmailLoginViewModel
import com.example.rushbuy.feature.auth.presentation.login.viewmodel.GoogleLoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (UserRole?) -> Unit,
    emailLoginViewModel: EmailLoginViewModel = koinViewModel(),
    googleLoginViewModel: GoogleLoginViewModel = koinViewModel()
) {
    val context = LocalContext.current

    val emailLoginState by emailLoginViewModel.uiState.collectAsStateWithLifecycle()
    val googleLoginState by googleLoginViewModel.uiState.collectAsStateWithLifecycle()

    var intendedRoleForGoogleLogin: UserRole? by remember { mutableStateOf(null) }

    LaunchedEffect(emailLoginState.errorMessage) {
        emailLoginState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            emailLoginViewModel.clearError()
        }
    }

    LaunchedEffect(googleLoginState.errorMessage) {
        googleLoginState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            googleLoginViewModel.clearError()
        }
    }

    LaunchedEffect(emailLoginState.isLoginSuccessful) {
        if (emailLoginState.isLoginSuccessful) {
            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
            onLoginSuccess(UserRole.USER)
        }
    }

    LaunchedEffect(googleLoginState.isLoginSuccessful) {
        if (googleLoginState.isLoginSuccessful) {
            Toast.makeText(context, "Google Login Successful!", Toast.LENGTH_SHORT).show()
            onLoginSuccess(intendedRoleForGoogleLogin)
            googleLoginViewModel.resetLoginState()
            intendedRoleForGoogleLogin = null
        }
    }

    val isAnyLoading = emailLoginState.isLoading || googleLoginState.isLoading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                    )
                )
            ),
        contentAlignment = Alignment.Center // Center the card
    ) {
        // --- REMOVED: RushBuy app name from here, as per your latest instruction ---

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp) // This provides the drop shadow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp)) // Maintain some top spacing within the card

                Text(
                    text = "Sign in to\ncontinue", // Use \n for line break as in screenshot
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Enter your credentials or choose a social login option.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Field
                OutlinedTextField(
                    value = emailLoginState.email,
                    onValueChange = emailLoginViewModel::updateEmail,
                    label = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Password Field
                OutlinedTextField(
                    value = emailLoginState.password,
                    onValueChange = emailLoginViewModel::updatePassword,
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (emailLoginState.isPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (emailLoginState.isPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff
                        val description = if (emailLoginState.isPasswordVisible)
                            "Hide password" else "Show password"
                        IconButton(onClick = emailLoginViewModel::togglePasswordVisibility) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Email Login Button
                Button(
                    onClick = emailLoginViewModel::login,
                    enabled = !isAnyLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    AnimatedVisibility(
                        visible = emailLoginState.isLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    }
                    AnimatedVisibility(
                        visible = !emailLoginState.isLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text("Login", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Divider (OR)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                    Text(
                        text = "OR",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                }

                // Google Login Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Google Login as User Button
                    OutlinedButton(
                        onClick = {
                            intendedRoleForGoogleLogin = UserRole.USER
                            googleLoginViewModel.signInWithGoogle(context)
                        },
                        enabled = !isAnyLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                    ) {
                        if (googleLoginState.isLoading && intendedRoleForGoogleLogin == UserRole.USER) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.google),
                                    contentDescription = "Google",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text("User", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Google Login as Admin Button
                    OutlinedButton(
                        onClick = {
                            intendedRoleForGoogleLogin = UserRole.ADMIN
                            googleLoginViewModel.signInWithGoogle(context)
                        },
                        enabled = !isAnyLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                    ) {
                        if (googleLoginState.isLoading && intendedRoleForGoogleLogin == UserRole.ADMIN) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.google),
                                    contentDescription = "Google",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text("Admin", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // "Don't have an account? Sign Up" text button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Sign Up",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onNavigateToRegister)
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun LoginScreenPreview() {
//    MaterialTheme {
//        LoginScreen(
//            onNavigateToRegister = {},
//            onLoginSuccess = { role ->
//                println("Login Success for role: $role")
//            }
//        )
//    }
//}