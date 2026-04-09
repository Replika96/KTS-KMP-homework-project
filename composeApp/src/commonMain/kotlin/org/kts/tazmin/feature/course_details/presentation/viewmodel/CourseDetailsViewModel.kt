package org.kts.tazmin.feature.course_details.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.feature.course_details.domain.repository.CourseDetailsRepository
import org.kts.tazmin.feature.course_details.domain.repository.CourseStructureRepository
import org.kts.tazmin.feature.course_details.presentation.state.CourseDetailsUiState

class CourseDetailsViewModel(
    private val courseDetailsRepository: CourseDetailsRepository,
    private val courseStructureRepository: CourseStructureRepository,
    private val courseId: Int
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CourseDetailsUiState> =
        combine(
            combine(
                courseDetailsRepository.observeHeader(courseId),
                courseDetailsRepository.observeInfo(courseId),
                courseDetailsRepository.observeCTA(courseId),
                courseStructureRepository.observeCourseStructure(courseId)
            ) { header, info, cta, modules ->
                Napier.d("UI combine: header=$header, info=$info, cta=$cta, modules=${modules.size}")
                CourseDetailsUiState(header = header, info = info, cta = cta, modules = modules)
            },
            _isRefreshing,
            _refreshError
        ) { courseState, refreshing, error ->
            courseState.copy(isRefreshing = refreshing, error = error)
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                CourseDetailsUiState()
            )

    fun refresh() {
        viewModelScope.launch {
            Napier.i("Refresh started for courseId=$courseId")

            _isRefreshing.value = true
            _refreshError.value = null

            val results = listOf(
                async {
                    Napier.d("Refreshing course details…")
                    runCatchingCancellable { courseDetailsRepository.refresh(courseId) }
                },
                async {
                    Napier.d("Refreshing course structure…")
                    runCatchingCancellable {
                        courseStructureRepository.refreshCourseStructure(
                            courseId
                        )
                    }
                }
            ).awaitAll()

            results.forEachIndexed { index, result ->
                if (result.isSuccess) {
                    Napier.i("Refresh part #$index completed successfully")
                } else {
                    Napier.e("Refresh part #$index failed", result.exceptionOrNull())
                }
            }

            results.firstOrNull { it.isFailure }
                ?.exceptionOrNull()
                ?.let { error ->
                    Napier.e("Refresh failed", error)
                    _refreshError.value = error.message
                }

            _isRefreshing.value = false

            Napier.i("Refresh finished")
        }
    }

    init {
        Napier.i("CourseDetailsViewModel init: auto refresh")
        refresh()
    }
}

