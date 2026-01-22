package com.bandverse.app.data.repository

import com.bandverse.app.data.local.SupabaseClient
import com.bandverse.app.data.model.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor() {

    suspend fun getProfile(userId: String): Result<Profile?> {
        return try {
            val filters = mapOf("id" to "eq.$userId")

            val response = SupabaseClient.get<Profile>(
                table = "profiles",
                filters = filters,
                limit = 1
            )

            Result.success(response.firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        userId: String,
        username: String? = null,
        avatarUrl: String? = null,
        bio: String? = null
    ): Result<Profile> {
        return try {
            val updateData = mutableMapOf<String, Any>()
            username?.let { updateData["username"] = it }
            avatarUrl?.let { updateData["avatar_url"] = it }
            bio?.let { updateData["bio"] = it }

            val filters = mapOf("id" to "eq.$userId")

            val response = SupabaseClient.patch<Profile>(
                table = "profiles",
                data = updateData,
                filters = filters
            )

            Result.success(response.first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
