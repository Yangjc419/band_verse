package com.bandverse.app.domain.usecase

import com.bandverse.app.data.model.UserRecommendation
import com.bandverse.app.data.repository.RecommendationRepository
import javax.inject.Inject

class GetUserRecommendationsUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(userId: String): Result<List<UserRecommendation>> {
        return repository.getUserRecommendations(userId)
    }
}
