package org.kts.tazmin.feature.feed.data.model

import org.kts.tazmin.feature.feed.domain.entity.Post

data class FeedResult(
    val posts: List<Post>,
    val after: String?
)
