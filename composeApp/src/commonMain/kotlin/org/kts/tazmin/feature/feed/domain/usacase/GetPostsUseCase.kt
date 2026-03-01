package org.kts.tazmin.feature.feed.domain.usacase

import org.kts.tazmin.feature.feed.data.model.FeedResult
import org.kts.tazmin.feature.feed.domain.repository.PostsRepository
import org.kts.tazmin.feature.feed.presentation.state.FeedType

class GetPostsUseCase(private val postsRepository: PostsRepository) {
    // пока что простой usecase
    suspend operator fun invoke(
        type: FeedType,
        after: String?,
        limit: Int
    ): Result<FeedResult> {
        return postsRepository.getFeed(
            type = type,
            after = after,
            limit = limit
        )
    }
}
