package com.bandverse.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
