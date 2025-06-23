import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rushbuy.feature.auth.presentation.register.state.RegisterUiState
import com.example.rushbuy.feature.auth.presentation.register.viewmodel.RegisterViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit,
) {
    val registerViewModel: RegisterViewModel = koinViewModel()
    val context = LocalContext.current
    val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()

    // Handle error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show() // Use LENGTH_SHORT for quick feedback
            registerViewModel.clearError() // Clear error after showing
        }
    }

    // Handle registration success
    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            Toast.makeText(context, "Registration Successful! Welcome to RushBuy!", Toast.LENGTH_SHORT).show()
            onRegistrationSuccess() // Trigger navigation
            registerViewModel.resetRegistrationState() // Reset ViewModel state after navigation
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f), // Consistent with LoginScreen
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f) // Consistent with LoginScreen
                    )
                )
            )
    ) {
        // Top App Bar
        TopAppBar(
            title = { /* No title as per instructions for login/register screens */ },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary // Use onPrimary for contrast with gradient
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent // Keep transparent to show gradient
            )
        )

        // Main Content - Centered Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Consistent with LoginScreen
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp) // Consistent with LoginScreen
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .verticalScroll(rememberScrollState()), // Allow scrolling for smaller screens
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp)) // Maintain top spacing within the card

                    // Title
                    Text(
                        text = "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface, // Consistent with LoginScreen
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Join RushBuy today",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Consistent with LoginScreen
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing

                    // Username Field
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = registerViewModel::updateUsername,
                        label = { Text("Username") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors( // Consistent styling
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    // Email Field
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = registerViewModel::updateEmail,
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors( // Consistent styling
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = registerViewModel::updatePassword,
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (uiState.isPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = registerViewModel::togglePasswordVisibility) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible)
                                        Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (uiState.isPasswordVisible)
                                        "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors( // Consistent styling
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    // Confirm Password Field
                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        onValueChange = registerViewModel::updateConfirmPassword,
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        visualTransformation = if (uiState.isConfirmPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = registerViewModel::toggleConfirmPasswordVisibility) {
                                Icon(
                                    imageVector = if (uiState.isConfirmPasswordVisible)
                                        Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (uiState.isConfirmPasswordVisible)
                                        "Hide confirm password" else "Show confirm password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors( // Consistent styling
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    // Address Field
                    OutlinedTextField(
                        value = uiState.address,
                        onValueChange = registerViewModel::updateAddress,
                        label = { Text("Address") },
                        minLines = 2,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors( // Consistent styling
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Register Button
                    Button(
                        onClick = registerViewModel::register,
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // Consistent height with LoginScreen
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        AnimatedVisibility( // Smooth loading animation
                            visible = uiState.isLoading,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp), // Consistent size
                                color = MaterialTheme.colorScheme.onPrimary, // Consistent color
                                strokeWidth = 2.dp
                            )
                        }
                        AnimatedVisibility( // Smooth text visibility
                            visible = !uiState.isLoading,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) // Consistent font size/weight
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Link
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant, // Consistent color
                            fontSize = 15.sp // Consistent font size
                        )
                        Text(
                            text = "Sign In", // Changed TextButton to clickable Text for consistency with LoginScreen's "Sign Up"
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onNavigateBack) // Navigates back to login
                        )
                    }
                }
            }
        }
    }
}

// Preview Functions (directly previewing RegisterScreen)
@Preview(showBackground = true, showSystemUi = true, name = "Register Screen Default")
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen(
            onNavigateBack = {},
            onRegistrationSuccess = {}
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true, name = "Register Screen Loading State")
//@Composable
//fun RegisterScreenLoadingPreview() {
//    MaterialTheme {
//        // Create a dummy ViewModel for preview with loading state
//        val dummyViewModel = object : RegisterViewModel(object : com.example.rushbuy.feature.auth.domain.usecase.RegisterUseCase(
//            object : com.example.rushbuy.data.auth.AuthRepositoryImpl(
//                object : com.google.firebase.auth.FirebaseAuth() { /* dummy */ },
//                object : com.google.firebase.firestore.FirebaseFirestore() { /* dummy */ }
//            ) {} // Using dummy for AuthRepository and Firebase instances
//        ) {}) {
//            init {
//                // Manually set initial state for preview
//                _uiState.value = RegisterUiState(
//                    username = "LoadingUser",
//                    email = "loading@example.com",
//                    isLoading = true
//                )
//            }
//        }
//        RegisterScreen(
//            onNavigateBack = {},
//            onRegistrationSuccess = {},
//            registerViewModel = dummyViewModel
//        )
//    }
//}

//@Preview(showBackground = true, showSystemUi = true, name = "Register Screen Error State")
//@Composable
//fun RegisterScreenErrorPreview() {
//    MaterialTheme {
//        val dummyViewModel = object : RegisterViewModel(object : com.example.rushbuy.feature.auth.domain.usecase.RegisterUseCase(
//            object : com.example.rushbuy.data.auth.AuthRepositoryImpl(
//                object : com.google.firebase.auth.FirebaseAuth() { /* dummy */ },
//                object : com.google.firebase.firestore.FirebaseFirestore() { /* dummy */ }
//            ) {}
//        ) {}) {
//            init {
//                _uiState.value = RegisterUiState(
//                    errorMessage = "Email already in use.",
//                    isLoading = false
//                )
//            }
//        }
//        RegisterScreen(
//            onNavigateBack = {},
//            onRegistrationSuccess = {},
//            registerViewModel = dummyViewModel
//        )
//    }