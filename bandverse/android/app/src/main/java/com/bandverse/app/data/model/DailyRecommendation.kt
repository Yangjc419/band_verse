package com.bandverse.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyRecommendation(
    val id: String,
    val recommendedDate: String,
    val userRecommendationId: String,
    val createdAt: String? = null,
    val user_recommendations: UserRecommendation? = null
)
