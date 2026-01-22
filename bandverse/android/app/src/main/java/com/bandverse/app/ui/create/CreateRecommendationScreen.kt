package com.bandverse.app.ui.create

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bandverse.app.data.model.Song
import com.bandverse.app.presentation.create.CreateRecommendationUiState
import com.bandverse.app.presentation.create.CreateRecommendationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecommendationScreen(
    viewModel: CreateRecommendationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLyricPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建推荐") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is CreateRecommendationUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CreateRecommendationUiState.SongsLoaded -> {
                SongList(
                    songs = state.songs,
                    onSongClick = { viewModel.selectSong(it) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is CreateRecommendationUiState.SongSelected -> {
                CreateRecommendationForm(
                    song = state.song,
                    selectedLyric = state.selectedLyric,
                    reason = state.reason,
                    onLyricClick = { showLyricPicker = true },
                    onReasonChange = { viewModel.updateReason(it) },
                    onSubmit = { viewModel.submitRecommendation() },
                    onBack = { viewModel.reset() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is CreateRecommendationUiState.Submitting -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("提交中...")
                    }
                }
            }
            is CreateRecommendationUiState.Success -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "推荐提交成功！",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.reset() }) {
                            Text("继续推荐")
                        }
                    }
                }
            }
            is CreateRecommendationUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.reset() }) {
                            Text("重试")
                        }
                    }
                }
            }
            is CreateRecommendationUiState.Initial -> {}
        }
    }

    if (showLyricPicker && uiState is CreateRecommendationUiState.SongSelected) {
        LyricPickerDialog(
            song = (uiState as CreateRecommendationUiState.SongSelected).song,
            selectedLyric = (uiState as CreateRecommendationUiState.SongSelected).selectedLyric,
            onLyricSelected = {
                viewModel.selectLyric(it)
                showLyricPicker = false
            },
            onDismiss = { showLyricPicker = false }
        )
    }
}

@Composable
fun SongList(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "选择歌曲",
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(songs) { song ->
            SongItem(
                song = song,
                onClick = { onSongClick(song) }
            )
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.album?.title ?: "Unknown Album",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun CreateRecommendationForm(
    song: Song,
    selectedLyric: String,
    reason: String,
    onLyricClick: () -> Unit,
    onReasonChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = song.title.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = song.album?.band?.name ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLyricClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (selectedLyric.isBlank()) "选择歌词" else "已选: ${selectedLyric.take(30)}...",
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = reason,
            onValueChange = onReasonChange,
            label = { Text("推荐理由（选填）") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedLyric.isNotBlank()
        ) {
            Text("提交推荐")
        }
    }
}

@Composable
fun LyricPickerDialog(
    song: Song,
    selectedLyric: String,
    onLyricSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val lyrics = song.lyrics?.lines()?.filter { it.isNotBlank() && it.length > 5 } ?: emptyList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择歌词") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(400.dp)
            ) {
                items(lyrics) { lyric ->
                    Card(
                        onClick = { onLyricSelected(lyric) },
                        colors = if (selectedLyric == lyric) {
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Text(
                            text = lyric,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
