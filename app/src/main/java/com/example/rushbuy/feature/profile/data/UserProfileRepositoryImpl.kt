package com.example.rushbuy.feature.profile.data

import com.example.rushbuy.core.foundation.utils.ResultState
import com.example.rushbuy.core.foundation.utils.toDomainUserProfile
import com.example.rushbuy.feature.profile.domain.model.UserProfile
import com.example.rushbuy.feature.profile.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth

class UserProfileRepositoryImpl(
    private val firebaseAuth: FirebaseAuth // Inject FirebaseAuth instance
) : UserProfileRepository {

    override suspend fun getUserProfile(): ResultState<UserProfile> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                ResultState.Success(firebaseUser.toDomainUserProfile())
            } else {
                // This case should ideally mean the user is not logged in,
                // or their session expired.
                ResultState.Error("User not logged in or session expired.")
            }
        } catch (e: Exception) {
            ResultState.Error("Failed to fetch user profile: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    override suspend fun logout(): ResultState<Unit> {
        return try {
            firebaseAuth.signOut()
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error("Failed to log out: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}