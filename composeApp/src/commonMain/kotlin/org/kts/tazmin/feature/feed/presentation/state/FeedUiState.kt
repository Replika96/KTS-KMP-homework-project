package org.kts.tazmin.feature.feed.presentation.state

import org.kts.tazmin.feature.feed.domain.entity.Post

data class FeedUiState(
    val posts: List<Post> = emptyList(),

    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,

    val feedError: String? = null,

    val currentFeedType: FeedType = FeedType.HOT,

    val after: String? = null,

    val searchQuery: String = "",
    val isSearching: Boolean = false
)

enum class FeedType {
    HOT, NEW, BEST, TOP
}
