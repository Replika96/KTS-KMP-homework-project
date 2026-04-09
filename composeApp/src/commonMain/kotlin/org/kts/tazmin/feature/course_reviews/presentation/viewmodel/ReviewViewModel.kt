package org.kts.tazmin.feature.course_reviews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kts.tazmin.feature.course_reviews.domain.repository.ReviewRepository
import org.kts.tazmin.feature.course_reviews.presentation.state.ReviewUiState

class ReviewViewModel(
    private val courseId: Long,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewUiState())
    val state: StateFlow<ReviewUiState> = _state.asStateFlow()

    init {
        observeReviews()
        refresh()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeReviews() {
        viewModelScope.launch {
            _state
                .map { it.selectedScore }
                .distinctUntilChanged()
                .flatMapLatest { score ->
                    reviewRepository.observeReviews(courseId, score)
                }
                .collect { list ->
                    _state.update { it.copy(reviews = list) }
                }
        }
    }

    fun refresh() {
        val score = _state.value.selectedScore
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            val result = reviewRepository.refreshReviews(
                courseId,
                score
            )

            _state.update {
                it.copy(
                    isRefreshing = false,
                    hasMore = result.getOrDefault(false),
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun loadNextPage() {
        val current = _state.value
        if (current.isLoadingMore || !current.hasMore) return
        val score = current.selectedScore
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }

            val result = reviewRepository.loadNextPage(courseId, score)

            _state.update {
                it.copy(
                    isLoadingMore = false,
                    hasMore = result.getOrDefault(false),
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun createReview(score: Int, text: String) {
        viewModelScope.launch {
            val result = reviewRepository.createReview(courseId, score, text)

            _state.update {
                it.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun vote(reviewId: Long, vote: String) {
        viewModelScope.launch {
            val result = reviewRepository.voteReview(reviewId, vote)

            _state.update {
                it.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun setScore(score: Int?) {
        _state.update {
            it.copy(selectedScore = score)
        }
    }
}
