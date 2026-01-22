package com.bandverse.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String,
    val bandId: String,
    val title: String,
    val releaseYear: Int? = null,
    val coverUrl: String? = null,
    val type: String = "album",
    val createdAt: String? = null,
    val band: Band? = null
)
