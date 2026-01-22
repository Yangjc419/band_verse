package com.bandverse.app.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandverse.app.data.model.Song
import com.bandverse.app.data.model.UserRecommendation
import com.bandverse.app.domain.usecase.GetAllSongsUseCase
import com.bandverse.app.domain.usecase.SubmitRecommendationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRecommendationViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val submitRecommendationUseCase: SubmitRecommendationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateRecommendationUiState>(CreateRecommendationUiState.Initial)
    val uiState: StateFlow<CreateRecommendationUiState> = _uiState.asStateFlow()

    private var currentUserId = "demo-user-id" // Replace with actual user ID from auth

    init {
        loadSongs()
    }

    fun loadSongs() {
        viewModelScope.launch {
            _uiState.value = CreateRecommendationUiState.Loading
            println("=== CreateRecommendationViewModel: Loading songs ===")
            getAllSongsUseCase()
                .onSuccess { songs ->
                    println("=== CreateRecommendationViewModel: Successfully loaded ${songs.size} songs ===")
                    songs.forEachIndexed { index, song ->
                        println("Song $index: ${song.title}")
                        println("  Album: ${song.album?.title}")
                        println("  Band: ${song.album?.band?.name}")
                    }
                    if (songs.isEmpty()) {
                        _uiState.value = CreateRecommendationUiState.Error("No songs available")
                    } else {
                        _uiState.value = CreateRecommendationUiState.SongsLoaded(songs)
                    }
                }
                .onFailure { error ->
                    println("=== CreateRecommendationViewModel: Failed to load songs ===")
                    println("Error: ${error.message}")
                    println("Stack trace: ${error.stackTraceToString()}")
                    _uiState.value = CreateRecommendationUiState.Error(error.message ?: "Failed to load songs")
                }
        }
    }

    fun selectSong(song: Song) {
        println("=== CreateRecommendationViewModel: Selected song: ${song.title} ===")
        val songsList = when (val currentState = _uiState.value) {
            is CreateRecommendationUiState.SongsLoaded -> currentState.songs
            is CreateRecommendationUiState.SongSelected -> currentState.songs
            else -> {
                println("=== CreateRecommendationViewModel: Warning - state is not SongsLoaded or SongSelected, loading songs ===")
                loadSongs()
                return
            }
        }

        _uiState.value = CreateRecommendationUiState.SongSelected(
            song = song,
            songs = songsList
        )
        println("=== CreateRecommendationViewModel: Song selected, songs list size: ${songsList.size} ===")
    }

    fun selectLyric(lyric: String) {
        val currentState = _uiState.value
        if (currentState is CreateRecommendationUiState.SongSelected) {
            _uiState.value = currentState.copy(selectedLyric = lyric)
        }
    }

    fun updateReason(reason: String) {
        val currentState = _uiState.value
        if (currentState is CreateRecommendationUiState.SongSelected) {
            _uiState.value = currentState.copy(reason = reason)
        }
    }

    fun submitRecommendation() {
        val currentState = _uiState.value
        if (currentState is CreateRecommendationUiState.SongSelected) {
            if (currentState.selectedLyric.isBlank()) {
                _uiState.value = CreateRecommendationUiState.Error("Please select a lyric")
                return
            }

            viewModelScope.launch {
                _uiState.value = CreateRecommendationUiState.Submitting
                submitRecommendationUseCase(
                    userId = currentUserId,
                    songId = currentState.song.id,
                    lyric = currentState.selectedLyric,
                    bandId = currentState.song.album?.bandId ?: "",
                    reason = currentState.reason.takeIf { it.isNotBlank() }
                )
                    .onSuccess {
                        _uiState.value = CreateRecommendationUiState.Success
                    }
                    .onFailure { error ->
                        _uiState.value = CreateRecommendationUiState.Error(error.message ?: "Failed to submit recommendation")
                    }
            }
        }
    }

    fun reset() {
        loadSongs()
    }
}

sealed class CreateRecommendationUiState {
    object Initial : CreateRecommendationUiState()
    object Loading : CreateRecommendationUiState()
    data class SongsLoaded(val songs: List<Song>) : CreateRecommendationUiState()
    data class SongSelected(
        val song: Song,
        val songs: List<Song>,
        val selectedLyric: String = "",
        val reason: String = ""
    ) : CreateRecommendationUiState()
    object Submitting : CreateRecommendationUiState()
    object Success : CreateRecommendationUiState()
    data class Error(val message: String) : CreateRecommendationUiState()
}
