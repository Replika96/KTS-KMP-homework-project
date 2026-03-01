package org.kts.tazmin.feature.feed.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import org.kts.tazmin.feature.feed.domain.entity.Post
import org.kts.tazmin.feature.feed.presentation.viewmodel.FeedViewModel
import org.kts.tazmin.theme.CatTheme

@Composable
fun FeedScreen(feedViewModel: FeedViewModel = koinInject()) {
    val state by feedViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        feedViewModel.loadPosts()
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.posts,
            key = { it.id } ) { post ->
            PostCard(post = post)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // аватарка сабреддита
                AsyncImage(
                    model = "https://www.redditstatic.com/avatars/avatar_default_02_FF4500.png",
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //сабреддит
                        Text(
                            text = "r/${post.subredcat}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = post.timeAgo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    // автор
                    Text(
                        text = "u/${post.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // кнопка подписки/меню
                IconButton(onClick = { /* подписаться */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // заголовок поста
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 20.sp
            )

            // контент поста (если есть)
            if (!post.content.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    maxLines = 5,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            // изображение (если есть)
            if (!post.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // голосование
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 4.dp)
                ) {
                    IconButton(
                        onClick = { /* апвоут */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Upvote",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = formatNumber(post.upvotes),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = { /* даунвоут */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Downvote",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = formatNumber(post.commentCount),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }


                Row {
                    IconButton(onClick = { /* наградить */ }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Award",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = { /* сохранить */ }) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { /* поделиться */ }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> {
            val millions = number / 1_000_000.0
            val rounded = (millions * 10).toInt() / 10.0
            "${rounded}M".replace('.', ',')
        }
        number >= 1_000 -> {
            val thousands = number / 1_000.0
            val rounded = (thousands * 10).toInt() / 10.0
            "${rounded}K".replace('.', ',')
        }
        else -> number.toString()
    }
}


@Preview(showBackground = true, name = "Redcat Feed")
@Composable
fun FeedScreenPreview() {
    CatTheme {
        val mockPosts = listOf(
            Post(
                id = "1",
                title = "Первый пост 🎉",
                author = "admin",
                subredcat = "cat",
                content = "Это наш первый пост в Reddit клиенте. Работает на CMP и будет доступен на Android и iOS(возможно)!",
                upvotes = 128,
                commentCount = 15,
                timeAgo = "2ч"
            ),
            Post(
                id = "2",
                title = "Кот на месте 🐱",
                author = "Кот Альберт",
                subredcat = "cat",
                content = "Мяу! Я пришел посмотреть что тут пишут. Всех обнял лапками.",
                upvotes = 356,
                commentCount = 42,
                imageUrl = "https://i.postimg.cc/1XV1P57q/anon.jpg",
                timeAgo = "5ч"
            ),
            Post(
                id = "3",
                title = "Как ваши дела?",
                author = "Аноним",
                subredcat = "cat",
                content = "Ребята, как настроение? Что читаете? Делитесь в комментариях!",
                upvotes = 89,
                commentCount = 23,
                imageUrl = "https://i.postimg.cc/cC7y65Zx/bingchilin.jpg",
                timeAgo = "12ч"
            ),
            Post(
                id = "4",
                title = "Kotlin Multiplatform это мощно!😮",
                author = "dev_guru",
                subredcat = "cat",
                content = "Пишем код один раз, а работает везде. CMP позволяет делать UI на всех платформах. Красота!",
                upvotes = 567,
                commentCount = 78,
                imageUrl = null,
                timeAgo = "1д"
            ),
            Post(
                id = "5",
                title = "Jetpack Compose vs SwiftUI",
                author = "mobile_dev",
                subredcat = "cat",
                content = "Интересно сравнить эти два фреймворка. CMP позволяет использовать Compose на iOS, но насколько это хорошо?",
                upvotes = 234,
                commentCount = 56,
                imageUrl = null,
                timeAgo = "2д"
            )
        )

        FeedContent(posts = mockPosts)
    }
}

@Composable
fun FeedContent(posts: List<Post>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(posts) { post ->
            PostCard(post = post)
        }
    }
}
