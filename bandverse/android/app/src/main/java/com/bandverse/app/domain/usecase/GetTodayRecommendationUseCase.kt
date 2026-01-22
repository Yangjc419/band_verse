package com.bandverse.app.domain.usecase

import com.bandverse.app.data.model.DailyRecommendationDetail
import com.bandverse.app.data.repository.RecommendationRepository
import javax.inject.Inject

class GetTodayRecommendationUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(): Result<DailyRecommendationDetail?> {
        return repository.getTodayRecommendation()
    }
}
