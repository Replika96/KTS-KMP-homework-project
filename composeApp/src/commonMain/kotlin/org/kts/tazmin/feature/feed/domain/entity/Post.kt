package org.kts.tazmin.feature.feed.domain.entity

import androidx.compose.runtime.Immutable

//@Immutable
data class Post(
    val id: String,
    val subredcat: String,
    val author: String,
    val title: String,
    val content: String? = null,
    val imageUrl: String? = null,
    val upvotes: Int,
    val commentCount: Int,
    val timeAgo: String,
    val avatarUrl: String? = null
)
