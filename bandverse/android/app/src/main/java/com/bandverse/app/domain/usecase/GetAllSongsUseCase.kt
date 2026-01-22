package com.bandverse.app.domain.usecase

import com.bandverse.app.data.model.Song
import com.bandverse.app.data.repository.RecommendationRepository
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(): Result<List<Song>> {
        return repository.getAllSongs()
    }
}
