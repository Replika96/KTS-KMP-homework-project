package org.kts.tazmin.feature.feed.domain.repository

import org.kts.tazmin.feature.feed.data.model.FeedResult
import org.kts.tazmin.feature.feed.presentation.state.FeedType

interface PostsRepository {
    suspend fun getFeed(
        type: FeedType,
        after: String?,
        limit: Int
    ): Result<FeedResult>
}
