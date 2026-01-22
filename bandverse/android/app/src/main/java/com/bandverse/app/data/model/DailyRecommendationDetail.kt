package com.bandverse.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyRecommendationDetail(
    val lyric: String,
    val reason: String?,
    val song: Song,
    val band: Band,
    val user: Profile,
    val createdAt: String? = null
)
