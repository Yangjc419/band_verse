package com.bandverse.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandverse.app.data.model.UserRecommendation
import com.bandverse.app.domain.usecase.GetUserRecommendationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getUserRecommendationsUseCase: GetUserRecommendationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var currentUserId = "demo-user-id" // Replace with actual user ID from auth

    init {
        loadUserRecommendations()
    }

fun loadUserRecommendations() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            println("=== HistoryViewModel: Loading recommendations for user: $currentUserId ===")
            getUserRecommendationsUseCase(currentUserId)
                .onSuccess { recommendations ->
                    println("=== HistoryViewModel: Successfully loaded ${recommendations.size} recommendations ===")
                    recommendations.forEachIndexed { index, rec ->
                        println("Recommendation $index: ${rec.lyric}")
                        println("  Song: ${rec.songs?.title}")
                        println("  Band: ${rec.bands?.name}")
                    }
                    if (recommendations.isEmpty()) {
                        _uiState.value = HistoryUiState.Empty
                    } else {
                        _uiState.value = HistoryUiState.Success(recommendations)
                    }
                }
                .onFailure { error ->
                    println("=== HistoryViewModel: Failed to load recommendations ===")
                    println("Error: ${error.message}")
                    println("Stack trace: ${error.stackTraceToString()}")
                    _uiState.value = HistoryUiState.Error(error.message ?: "Failed to load recommendations")
                }
        }
    }

    fun onRefresh() {
        loadUserRecommendations()
    }
}

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val recommendations: List<UserRecommendation>) : HistoryUiState()
    object Empty : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}
