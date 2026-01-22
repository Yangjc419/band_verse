package com.bandverse.app.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bandverse.app.data.model.UserRecommendation
import com.bandverse.app.presentation.history.HistoryUiState
import com.bandverse.app.presentation.history.HistoryViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = uiState is HistoryUiState.Loading

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.onRefresh() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is HistoryUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
                is HistoryUiState.Success -> {
                    RecommendationHistoryList(recommendations = state.recommendations)
                }
                is HistoryUiState.Empty -> {
                    Text(
                        text = "暂无推荐历史",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is HistoryUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.onRefresh() }) {
                            Text("重试")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationHistoryList(recommendations: List<UserRecommendation>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "我的推荐历史",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(recommendations) { recommendation ->
            RecommendationHistoryItem(recommendation = recommendation)
        }
    }
}

@Composable
fun RecommendationHistoryItem(recommendation: UserRecommendation) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "\"${recommendation.lyric}\"",
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "出自：${recommendation.songs?.title ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "乐队：${recommendation.bands?.name ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = when (recommendation.status) {
                    "approved" -> MaterialTheme.colorScheme.primaryContainer
                    "pending" -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.errorContainer
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = when (recommendation.status) {
                        "approved" -> "已审核"
                        "pending" -> "待审核"
                        else -> "已拒绝"
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            recommendation.createdAt?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it.substringBefore("T"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
