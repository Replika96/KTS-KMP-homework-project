package org.kts.tazmin.feature.feed.presentation.state

sealed interface FeedUiEvent {
    // загрузка
    object LoadPosts : FeedUiEvent
    object LoadMorePosts : FeedUiEvent
    object RefreshPosts : FeedUiEvent

    data class OnPostClick(val postId: String) : FeedUiEvent
    data class OnVoteUp(val postId: String) : FeedUiEvent
    data class OnVoteDown(val postId: String) : FeedUiEvent
    data class OnSavePost(val postId: String) : FeedUiEvent
    data class OnSharePost(val postId: String) : FeedUiEvent

    data class ChangeFeedType(val type: FeedType) : FeedUiEvent

    data class SearchQueryChanged(val query: String) : FeedUiEvent
    object ClearSearch : FeedUiEvent
}
