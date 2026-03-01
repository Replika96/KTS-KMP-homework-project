package org.kts.tazmin.feature.feed.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.feature.feed.domain.usacase.GetPostsUseCase
import org.kts.tazmin.feature.feed.presentation.state.FeedUiState

class FeedViewModel(private val getPostsUseCase: GetPostsUseCase): ViewModel() {

    private val _state = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

     fun loadPosts(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, feedError = null) }

            val currentState = _state.value
            val result = getPostsUseCase(
                type = currentState.currentFeedType,
                after = null,
                limit = 20
            )
            result.fold(
                onSuccess = { feedResult ->
                    _state.update {
                        it.copy(
                            posts = feedResult.posts,
                            after = feedResult.after,
                            isLoading = false
                        )
                    }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            feedError = throwable.message
                        )
                    }
                }
            )
        }
    }
}
