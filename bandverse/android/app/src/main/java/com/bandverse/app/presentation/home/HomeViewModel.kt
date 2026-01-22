package com.bandverse.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandverse.app.data.model.DailyRecommendationDetail
import com.bandverse.app.domain.usecase.GetTodayRecommendationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayRecommendationUseCase: GetTodayRecommendationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTodayRecommendation()
    }

    fun loadTodayRecommendation() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getTodayRecommendationUseCase()
                .onSuccess { recommendation ->
                    _uiState.value = if (recommendation != null) {
                        HomeUiState.Success(recommendation)
                    } else {
                        HomeUiState.NoRecommendation
                    }
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun onRefresh() {
        loadTodayRecommendation()
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val recommendation: DailyRecommendationDetail) : HomeUiState()
    object NoRecommendation : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
