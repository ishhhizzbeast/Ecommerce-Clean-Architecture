package com.example.rushbuy.feature.auth.data

//import com.example.rushbuy.feature.auth.domain.model.GoogleLoginRequest
//import com.example.rushbuy.feature.auth.domain.model.LoginRequest
//import com.example.rushbuy.feature.auth.domain.model.RegisterRequest
//import com.example.rushbuy.feature.auth.domain.model.UserRole
import com.example.rushbuy.feature.auth.domain.model.AuthResult
import com.example.rushbuy.feature.auth.domain.model.User
import com.example.rushbuy.feature.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val user = getUserFromFirestore(firebaseUser.uid)
                if (user != null) {
                    AuthResult.Success(user, "Login successful.")
                } else {
                    // User authenticated with Firebase, but their profile isn't in Firestore.
                    // This could happen if they registered directly with Firebase without saving to Firestore.
                    AuthResult.Error("User profile not found. Please contact support or re-register.")
                }
            } else {
                AuthResult.Error("Authentication failed: No Firebase user returned.")
            }
        } catch (e: Exception) {
            // Log the exception for debugging purposes: Log.e("AuthRepository", "Login error", e)
            AuthResult.Error(e.message ?: "An unknown error occurred during login.")
        }
    }

    override suspend fun signInWithGoogleCredential(idToken: String): AuthResult {
        return try {
            // Use the ID token obtained from Google Sign-In client flow
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()

            val firebaseUser = result.user
            if (firebaseUser != null) {
                // Check if user's custom profile already exists in Firestore
                var user = getUserFromFirestore(firebaseUser.uid)

                if (user == null) {
                    // If not, create a new custom profile for this Google-authenticated user
                    user = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Google User", // Get display name from Firebase
                        email = firebaseUser.email ?: "",
                        address = null, // Address might be null initially for Google sign-ins
                        // Assign the role based on the button clicked (passed from UI)
                        // role = role
                    )
                    saveUserToFirestore(user)
                    AuthResult.Success(user, "Google login successful (new user).")
                } else {
                    AuthResult.Success(user, "Google login successful (existing user).")
                }
            } else {
                AuthResult.Error("Google authentication failed: No Firebase user returned.")
            }
        } catch (e: Exception) {
            // Log the exception: Log.e("AuthRepository", "Google sign-in error", e)
            AuthResult.Error(e.message ?: "An unknown error occurred during Google sign-in.")
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        address: String?
    ): AuthResult {
        return try {
            // 1. Create user in Firebase Authentication
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // 2. Optionally, update Firebase user's display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await() // Apply display name

                // 3. Create your custom user profile in Firestore
                val newUser = User(
                    id = firebaseUser.uid,
                    name = username, // Use the username from registration form
                    email = email,   // Use the email from registration form
                    address = address
                )

                saveUserToFirestore(newUser) // Save the custom user profile
                AuthResult.Success(newUser, "Registration successful. Welcome!")
            } else {
                AuthResult.Error("Registration failed: No user created.")
            }
        } catch (e: Exception) {
            // Log the exception for debugging: Log.e("AuthRepository", "Registration error", e)
            AuthResult.Error(e.message ?: "An unknown error occurred during registration.")
        }
    }

    override suspend fun logout(): AuthResult {
        return try {
            firebaseAuth.signOut()
            // Return a dummy User object or just Success state as the user is logged out.
            // Ensure your User constructor can handle empty strings if you use them here.
            AuthResult.Success(User("", "", "", ""), "Logged out successfully.")
        } catch (e: Exception) {
            // Log the exception: Log.e("AuthRepository", "Logout error", e)
            AuthResult.Error(e.message ?: "Logout failed.")
        }
    }
    override suspend fun getCurrentUser(): User? {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Attempt to get the custom user profile from Firestore
                getUserFromFirestore(firebaseUser.uid)
            } else {
                null // No Firebase user currently authenticated
            }
        } catch (e: Exception) {
            // Log the exception: Log.e("AuthRepository", "Get current user error", e)
            null // Return null if any error occurs
        }
    }

    private suspend fun getUserFromFirestore(userId: String): User? {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                // Map Firestore document data to your User domain model
                User(
                    id = document.getString("id") ?: "",
                    name = document.getString("name") ?: "",
                    email = document.getString("email") ?: "",
                    address = document.getString("address"),
                    // If UserRole is part of your User model, re-add it here:
                    // role = UserRole.valueOf(document.getString("role") ?: "USER")
                )
            } else {
                null // Document does not exist
            }
        } catch (e: Exception) {
            // Log the exception: Log.e("AuthRepository", "Error fetching user from Firestore", e)
            null
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        return try {
            val userMap = hashMapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "address" to user.address,
                // If UserRole is part of your User model, re-add it here:
                // "role" to user.role.name
            )

            firestore.collection("users")
                .document(user.id)
                .set(userMap) // Set will create or overwrite the document
                .await()
        } catch (e: Exception) {
            // Log the exception: Log.e("AuthRepository", "Error saving user to Firestore", e)
            throw e // Re-throw to allow calling functions to handle it
        } as Unit
    }

}
// --- Private Helper Functions for Firestore ---

