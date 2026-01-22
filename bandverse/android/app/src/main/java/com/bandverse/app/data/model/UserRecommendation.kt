package com.bandverse.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRecommendation(
    val id: String,
    val userId: String,
    val songId: String,
    val lyric: String,
    val bandId: String,
    val recommendationReason: String? = null,
    val status: String = "pending",
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val profiles: Profile? = null,
    val songs: Song? = null,
    val bands: Band? = null
)
