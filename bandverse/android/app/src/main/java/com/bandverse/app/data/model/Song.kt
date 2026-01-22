package com.bandverse.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val albumId: String,
    val title: String,
    val duration: Int? = null,
    val lyrics: String? = null,
    val spotifyUrl: String? = null,
    val youtubeUrl: String? = null,
    val createdAt: String? = null,
    val album: Album? = null
)
