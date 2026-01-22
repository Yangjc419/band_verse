package com.bandverse.app.domain.usecase

import com.bandverse.app.data.model.UserRecommendation
import com.bandverse.app.data.repository.RecommendationRepository
import javax.inject.Inject

class SubmitRecommendationUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(
        userId: String,
        songId: String,
        lyric: String,
        bandId: String,
        reason: String?
    ): Result<UserRecommendation> {
        return repository.submitRecommendation(userId, songId, lyric, bandId, reason)
    }
}
