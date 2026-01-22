package com.bandverse.app.data.repository

import com.bandverse.app.data.local.SupabaseClient
import com.bandverse.app.data.model.Album
import com.bandverse.app.data.model.DailyRecommendation
import com.bandverse.app.data.model.DailyRecommendationDetail
import com.bandverse.app.data.model.Song
import com.bandverse.app.data.model.UserRecommendation
import com.bandverse.app.data.model.Profile
import com.bandverse.app.data.model.Band
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class UserRecommendationWithDetails(
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

@Serializable
data class DailyRecommendationWithFullDetails(
    val id: String,
    val recommendedDate: String,
    val userRecommendationId: String,
    val createdAt: String? = null,
    val user_recommendations: UserRecommendationWithDetails? = null
)

@Singleton
class RecommendationRepository @Inject constructor() {
    
    // 预设数据 - 用于演示和测试
    private val mockAlbums = listOf(
        Album(
            id = "album-1",
            bandId = "band-1",
            title = "Nevermind",
            releaseYear = 1991,
            type = "album",
            coverUrl = "https://example.com/nevermind.jpg",
            createdAt = "1991-09-24T00:00:00Z",
            band = Band(
                id = "band-1",
                name = "Nirvana"
            )
        ),
        Album(
            id = "album-2",
            bandId = "band-2",
            title = "Elephant",
            releaseYear = 2003,
            type = "album",
            coverUrl = "https://example.com/elephant.jpg",
            createdAt = "2003-04-01T00:00:00Z",
            band = Band(
                id = "band-2",
                name = "The White Stripes"
            )
        ),
        Album(
            id = "album-3",
            bandId = "band-3",
            title = "Abbey Road",
            releaseYear = 1969,
            type = "album",
            coverUrl = "https://example.com/abbeyroad.jpg",
            createdAt = "1969-09-26T00:00:00Z",
            band = Band(
                id = "band-3",
                name = "The Beatles"
            )
        ),
        Album(
            id = "album-4",
            bandId = "band-4",
            title = "The Dark Side of the Moon",
            releaseYear = 1973,
            type = "album",
            coverUrl = "https://example.com/darkside.jpg",
            createdAt = "1973-03-01T00:00:00Z",
            band = Band(
                id = "band-4",
                name = "Pink Floyd"
            )
        ),
        Album(
            id = "album-5",
            bandId = "band-5",
            title = "Whatever People Say I Am, That's What I'm Not",
            releaseYear = 2006,
            type = "album",
            coverUrl = "https://example.com/whatever.jpg",
            createdAt = "2006-01-23T00:00:00Z",
            band = Band(
                id = "band-5",
                name = "Arctic Monkeys"
            )
        )
    )
    
    private val mockSongs = listOf(
        Song(
            id = "song-1",
            albumId = "album-1",
            title = "Smells Like Teen Spirit",
            duration = 301,
            lyrics = "Load up on guns, bring your friends\nIt's fun to lose and to pretend\nShe's over-bored and self-assured\nOh no, I know a dirty word",
            spotifyUrl = "https://open.spotify.com/track/3n3Ppam7vgaVa1iaRUc9Lp",
            youtubeUrl = "https://www.youtube.com/watch?v=hTWKbfoikeg",
            createdAt = "2024-01-15T10:00:00Z",
            album = mockAlbums[0]
        ),
        Song(
            id = "song-2",
            albumId = "album-2",
            title = "Seven Nation Army",
            duration = 233,
            lyrics = "I'm gonna fight 'em off\nA seven nation army couldn't hold me back\nThey're gonna rip it off\nTaking their time right behind my back",
            spotifyUrl = "https://open.spotify.com/track/6AZKXb3Gk7dS5w3aZ7b0g",
            youtubeUrl = "https://www.youtube.com/watch?v=0J2QdDbmNg0",
            createdAt = "2024-01-16T14:30:00Z",
            album = mockAlbums[1]
        ),
        Song(
            id = "song-3",
            albumId = "album-3",
            title = "Come Together",
            duration = 279,
            lyrics = "Here come old flattop, he come grooving up slowly\nHe got joo-joo eyeball, he one holy roller\nHe got hair down to his knee\nGot to be a joker, he just do what he please",
            spotifyUrl = "https://open.spotify.com/track/4TbZ1K6mF5dG7hT6k6g8",
            youtubeUrl = "https://www.youtube.com/watch?v=-_q4n6V3hH0",
            createdAt = "2024-01-17T09:15:00Z",
            album = mockAlbums[2]
        ),
        Song(
            id = "song-4",
            albumId = "album-4",
            title = "Time",
            duration = 396,
            lyrics = "Ticking away, the moments that make up a dull day\nFritter and waste the hours in an offhand way\nKicking around on a piece of ground in your hometown\nWaiting for someone or something to show you the way",
            spotifyUrl = "https://open.spotify.com/track/5K6L7L8k6j8k7l8l9k0",
            youtubeUrl = "https://www.youtube.com/watch?v=IXdzV2bL5lI",
            createdAt = "2024-01-18T16:45:00Z",
            album = mockAlbums[3]
        ),
        Song(
            id = "song-5",
            albumId = "album-5",
            title = "I Bet You Look Good on the Dancefloor",
            duration = 174,
            lyrics = "Stop making the eyes at me\nI'll stop making my eyes at you\nWhat it is that surprises me\nIs that I don't really want you to",
            spotifyUrl = "https://open.spotify.com/track/7L8M9M0l7k8l9m0m1n0",
            youtubeUrl = "https://www.youtube.com/watch?v=1Ph0reS8I1c",
            createdAt = "2024-01-19T11:20:00Z",
            album = mockAlbums[4]
        )
    )
    
    private val mockBands = listOf(
        Band(
            id = "band-1",
            name = "Nirvana"
        ),
        Band(
            id = "band-2",
            name = "The White Stripes"
        ),
        Band(
            id = "band-3",
            name = "The Beatles"
        ),
        Band(
            id = "band-4",
            name = "Pink Floyd"
        ),
        Band(
            id = "band-5",
            name = "Arctic Monkeys"
        )
    )
    
    private val mockProfiles = listOf(
        Profile(
            id = "user-1",
            username = "music_lover"
        ),
        Profile(
            id = "user-2",
            username = "rock_fanatic"
        ),
        Profile(
            id = "user-3",
            username = "indie_vibes"
        )
    )
    
    private val mockUserRecommendations = listOf(
        UserRecommendation(
            id = "rec-1",
            userId = "user-1",
            songId = "song-1",
            lyric = "With the lights out, it's less dangerous",
            bandId = "band-1",
            recommendationReason = "这首歌定义了一个世代。激情和原始情感无与伦比。",
            status = "approved",
            songs = mockSongs[0],
            bands = mockBands[0],
            createdAt = "2024-01-15T10:00:00Z"
        ),
        UserRecommendation(
            id = "rec-2",
            userId = "user-2",
            songId = "song-2",
            lyric = "A seven nation army couldn't hold me back",
            bandId = "band-2",
            recommendationReason = "那个贝斯线是传奇。两人乐队做出体育场摇滚。",
            status = "approved",
            songs = mockSongs[1],
            bands = mockBands[1],
            createdAt = "2024-01-16T14:30:00Z"
        ),
        UserRecommendation(
            id = "rec-3",
            userId = "user-3",
            songId = "song-3",
            lyric = "Come together, right now, over me",
            bandId = "band-3",
            recommendationReason = "这首歌捕捉了团结和聚会的本质。贝斯线是纯粹的魔力。",
            status = "pending",
            songs = mockSongs[2],
            bands = mockBands[2],
            createdAt = "2024-01-17T09:15:00Z"
        ),
        UserRecommendation(
            id = "rec-4",
            userId = "user-1",
            songId = "song-4",
            lyric = "And then one day you find, ten years have got behind you",
            bandId = "band-4",
            recommendationReason = "对时间流逝的深刻反思。开头的钟声令人难忘。",
            status = "approved",
            songs = mockSongs[3],
            bands = mockBands[3],
            createdAt = "2024-01-18T16:45:00Z"
        ),
        UserRecommendation(
            id = "rec-5",
            userId = "user-2",
            songId = "song-5",
            lyric = "Lighting the fuse might result in a bang with a bang-go",
            bandId = "band-5",
            recommendationReason = "犀利的歌词和朗朗上口的即兴重复段。Arctic Monkeys 的最佳状态。",
            status = "pending",
            songs = mockSongs[4],
            bands = mockBands[4],
            createdAt = "2024-01-19T11:20:00Z"
        )
    )

    suspend fun getTodayRecommendation(): Result<DailyRecommendationDetail?> {
        return try {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            println("Fetching today's recommendation for date: $today")

            val filters = mapOf("recommended_date" to "eq.$today")

            val response = SupabaseClient.get<DailyRecommendationWithFullDetails>(
                table = "daily_recommendations",
                filters = filters,
                limit = 1,
                select = "*, user_recommendations(*, profiles(*), songs(*, albums(*, bands(*))), bands(*)"
            )

            val detail = response.firstOrNull()?.let { dr ->
                println("Found recommendation: ${dr.id}")
                val ur = dr.user_recommendations
                DailyRecommendationDetail(
                    lyric = ur?.lyric ?: "",
                    reason = ur?.recommendationReason,
                    song = ur?.songs ?: Song("", "", "",  null, null, null, null),
                    band = ur?.bands ?: Band("", ""),
                    user = ur?.profiles ?: Profile("", ""),
                    createdAt = dr.createdAt
                )
            }

            println("Response size: ${response.size}, Detail: ${detail != null}")
            
            if (detail != null) {
                Result.success(detail)
            } else {
                // 如果没有找到今日推荐，返回预设推荐
                println("No recommendation found for today, returning mock recommendation")
                val mockRecommendation = DailyRecommendationDetail(
                    lyric = "A seven nation army couldn't hold me back",
                    reason = "那个贝斯线是传奇。两人乐队做出体育场摇滚。",
                    song = mockSongs[1],
                    band = mockBands[1],
                    user = mockProfiles[1],
                    createdAt = Clock.System.now().toString()
                )
                Result.success(mockRecommendation)
            }
        } catch (e: Exception) {
            println("Error fetching recommendation: ${e.message}")
            println("Returning mock recommendation...")
            // 出错时返回预设推荐
            val mockRecommendation = DailyRecommendationDetail(
                lyric = "A seven nation army couldn't hold me back",
                reason = "那个贝斯线是传奇。两人乐队做出体育场摇滚。",
                song = mockSongs[1],
                band = mockBands[1],
                user = mockProfiles[1],
                createdAt = Clock.System.now().toString()
            )
            Result.success(mockRecommendation)
        }
    }

    suspend fun getAllSongs(): Result<List<Song>> {
        return try {
            println("=== Repository: Fetching all songs ===")
            val response = SupabaseClient.get<Song>(
                table = "songs",
                limit = 100,
                select = "*, albums(*, bands(*))"
            )
            println("=== Repository: Fetched ${response.size} songs from Supabase ===")
            
            if (response.isEmpty()) {
                println("=== Repository: No songs from Supabase, returning mock data ===")
                Result.success(mockSongs)
            } else {
                println("=== Repository: Using Supabase data ===")
                Result.success(response)
            }
        } catch (e: Exception) {
            println("=== Repository: Error fetching songs: ${e.message} ===")
            println("=== Repository: Returning mock data... ===")
            // 返回预设数据
            Result.success(mockSongs)
        }
    }

    suspend fun submitRecommendation(
        userId: String,
        songId: String,
        lyric: String,
        bandId: String,
        reason: String?
    ): Result<UserRecommendation> {
        return try {
            println("=== Repository: Submitting recommendation ===")
            println("User ID: $userId")
            println("Song ID: $songId")
            println("Lyric: $lyric")
            println("Band ID: $bandId")
            println("Reason: $reason")

            val insertData = mapOf(
                "user_id" to userId,
                "song_id" to songId,
                "lyric" to lyric,
                "band_id" to bandId,
                "recommendation_reason" to reason,
                "status" to "pending"
            )

            val response = SupabaseClient.post<UserRecommendation>(
                table = "user_recommendations",
                data = insertData,
                select = "*"
            )

            println("=== Repository: Post response size: ${response.size} ===")

            if (response.isNotEmpty()) {
                println("=== Repository: Recommendation submitted successfully ===")
                Result.success(response.first())
            } else {
                println("=== Repository: Empty response from Supabase, returning mock recommendation ===")
                // 返回模拟的推荐结果
                val mockRecommendation = UserRecommendation(
                    id = "mock-rec-${System.currentTimeMillis()}",
                    userId = userId,
                    songId = songId,
                    lyric = lyric,
                    bandId = bandId,
                    recommendationReason = reason,
                    status = "pending",
                    createdAt = Clock.System.now().toString(),
                    songs = mockSongs.find { it.id == songId },
                    bands = mockBands.find { it.id == bandId }
                )
                Result.success(mockRecommendation)
            }
        } catch (e: Exception) {
            println("=== Repository: Error submitting recommendation: ${e.message} ===")
            println("=== Repository: Stack trace: ${e.stackTraceToString()} ===")
            // 返回模拟的推荐结果
            val mockRecommendation = UserRecommendation(
                id = "mock-rec-${System.currentTimeMillis()}",
                userId = userId,
                songId = songId,
                lyric = lyric,
                bandId = bandId,
                recommendationReason = reason,
                status = "pending",
                createdAt = Clock.System.now().toString(),
                songs = mockSongs.find { it.id == songId },
                bands = mockBands.find { it.id == bandId }
            )
            Result.success(mockRecommendation)
        }
    }

    suspend fun getUserRecommendations(userId: String): Result<List<UserRecommendation>> {
        return try {
            println("=== Repository: Fetching recommendations for user: $userId ===")
            val filters = mapOf("user_id" to "eq.$userId")
            val response = SupabaseClient.get<UserRecommendation>(
                table = "user_recommendations",
                filters = filters,
                limit = 100,
                select = "*, songs(*), bands(*)"
            )
            println("=== Repository: Fetched ${response.size} recommendations for user $userId ===")
            
            if (response.isEmpty()) {
                println("=== Repository: No recommendations from Supabase, returning mock data ===")
                Result.success(mockUserRecommendations)
            } else {
                println("=== Repository: Using Supabase data ===")
                Result.success(response)
            }
        } catch (e: Exception) {
            println("=== Repository: Error fetching user recommendations: ${e.message} ===")
            println("=== Repository: Returning mock recommendations for user: $userId ===")
            // 返回预设数据
            Result.success(mockUserRecommendations)
        }
    }

    suspend fun likeRecommendation(userId: String, recommendationId: String): Result<Boolean> {
        return try {
            val insertData = mapOf(
                "user_id" to userId,
                "recommendation_id" to recommendationId,
                "interaction_type" to "like"
            )

            SupabaseClient.post<Map<String, Any>>(
                table = "recommendation_interactions",
                data = insertData
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
