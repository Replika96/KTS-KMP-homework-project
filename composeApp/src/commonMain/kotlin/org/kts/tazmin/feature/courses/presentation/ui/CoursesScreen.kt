package org.kts.tazmin.feature.courses.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.continue_coures
import ktskotlinproject.composeapp.generated.resources.main_screen
import ktskotlinproject.composeapp.generated.resources.my_active_courses
import ktskotlinproject.composeapp.generated.resources.my_reviews
import ktskotlinproject.composeapp.generated.resources.no_active_courses
import ktskotlinproject.composeapp.generated.resources.wishlist
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.kts.tazmin.feature.courses.domain.entity.Course
import org.kts.tazmin.feature.courses.presentation.viewmodel.CoursesViewModel
import org.kts.tazmin.theme.CatNose
import org.kts.tazmin.theme.CatTheme
import org.kts.tazmin.theme.CozyBrown
import org.kts.tazmin.theme.CreamPaws
import org.kts.tazmin.theme.GingerCat
import org.kts.tazmin.theme.SoftWhiskers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = koinInject(),
    onCourseClick: (Int) -> Unit = {},
    onAllCoursesClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onReviewsClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel){
        viewModel.loadCourses()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.main_screen),
                        color = CozyBrown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = { /* уведомления */ }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = CozyBrown
                        )
                    }
                    IconButton(onClick = { /* настройки */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = CozyBrown
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CreamPaws,
                    scrolledContainerColor = CreamPaws
                )
            )
        },
        containerColor = CreamPaws
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // карточка "Продолжить учиться"
            item {
                ContinueLearningCard(
                    course = state.courses.firstOrNull(),
                    modifier = Modifier.padding(16.dp)
                )
            }

            // раздел "Мои курсы"
            item {
                MyCoursesHeader(
                    count = state.courses.size,
                    onViewAllClick = onAllCoursesClick,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // список моих курсов
            items(
                items = state.courses,
                key = { "my_course_${it.id}" }
            ) { course ->
                MyCourseItem(
                    course = course,
                    onContinueClick = { onCourseClick(course.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // "Мои отзывы" и "Список желаний"
            item {
                ActionCards(
                    onReviewsClick = onReviewsClick,
                    onWishlistClick = onWishlistClick,
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
    }
}

@Composable
fun ContinueLearningCard(
    course: Course?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // картинка курса
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftWhiskers)
            ) {
                if (course?.coverUrl != null) {
                    AsyncImage(
                        model = course.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GingerCat.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📚",
                            fontSize = 30.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // контент справа
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Button(
                    onClick = { /* продолжить */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GingerCat,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.continue_coures),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // название курса
                Text(
                    text = course?.title ?: stringResource(Res.string.no_active_courses),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CozyBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // прогресс
                if (course != null) {
                    Column {
                        LinearProgressIndicator(
                            progress = { 0.45f }, // мок
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GingerCat,
                            trackColor = SoftWhiskers
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "45% • 3/7 уроков",
                            fontSize = 11.sp,
                            color = CozyBrown.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyCoursesHeader(
    count: Int,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.my_active_courses),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CozyBrown
            )

            Spacer(modifier = Modifier.width(8.dp))

            // количество курсов
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(GingerCat),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // все курсы
        TextButton(
            onClick = onViewAllClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = GingerCat
            )
        ) {
            Text("Все")
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun MyCourseItem(
    course: Course,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // верхняя часть: картинка + название и прогресс
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // картинка
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SoftWhiskers)
                ) {
                    if (course.coverUrl != null) {
                        AsyncImage(
                            model = course.coverUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(GingerCat.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📚",
                                fontSize = 24.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // название и прогресс
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = course.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CozyBrown,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // прогресс
                    Column {
                        LinearProgressIndicator(
                            progress = { 0.3f }, // мок
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = GingerCat,
                            trackColor = SoftWhiskers
                        )

                        Text(
                            text = "30%",
                            fontSize = 11.sp,
                            color = CozyBrown.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // кнопка "Продолжить учиться"
            Button(
                onClick = onContinueClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GingerCat.copy(alpha = 0.1f),
                    contentColor = GingerCat
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Text(
                    text = stringResource(Res.string.continue_coures),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ActionCards(
    onReviewsClick: () -> Unit,
    onWishlistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // карточка "Мои отзывы"
        ActionCard(
            title = stringResource(Res.string.my_reviews),
            icon = Icons.Outlined.RateReview,
            iconColor = CatNose,
            onClick = onReviewsClick,
            modifier = Modifier.weight(1f)
        )

        // карточка "Список желаний"
        ActionCard(
            title = stringResource(Res.string.wishlist),
            icon = Icons.Outlined.FavoriteBorder,
            iconColor = GingerCat,
            count = 3, // пример количества
            onClick = onWishlistClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    count: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CozyBrown
                )

                if (count != null) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(GingerCat),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = count.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoursesScreenPreview() {
    CatTheme{
        val mockCourses = listOf(
            Course(
                id = 1,
                title = "Android Development with Kotlin",
                description = "Полный курс по разработке Android приложений",
                author = "JetBrains Academy",
                coverUrl = "https://i.postimg.cc/cC7y65Zx/bingchilin.jpg",
                rating = 4.8,
                studentsCount = 12000
            ),
            Course(
                id = 2,
                title = "Kotlin Multiplatform",
                description = "Изучаем KMP и создаем кроссплатформенные приложения",
                author = "JetBrains",
                coverUrl = "https://i.postimg.cc/1XV1P57q/anon.jpg",
                rating = 4.7,
                studentsCount = 8500
            )
        )
        // пока думаю как сделать
        CoursesScreen()
    }
}
