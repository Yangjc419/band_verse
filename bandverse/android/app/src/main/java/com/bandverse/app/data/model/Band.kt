package com.bandverse.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Band(
    val id: String,
    val name: String,
    val formationYear: Int? = null,
    val country: String? = null,
    val city: String? = null,
    val bio: String? = null,
    val imageUrl: String? = null,
    val genres: List<String> = emptyList(),
    val status: String = "active",
    val createdAt: String? = null
)
