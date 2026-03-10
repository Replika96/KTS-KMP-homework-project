package org.kts.tazmin.feature.auth.presentation.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.stepik_logotype_blac
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.kts.tazmin.theme.CatTheme
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit
) {

    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            val pageOffset = (
                    (pagerState.currentPage - page) +
                            pagerState.currentPageOffsetFraction
                    ).absoluteValue

            val scale = lerp(
                start = 0.85f,
                stop = 1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f)
            )

            val alpha = lerp(
                start = 0.5f,
                stop = 1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f)
            )

            val item = onboardingPages[page]

            Column(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(item.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(220.dp)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // индикаторы
        PagerIndicator(
            pageCount = onboardingPages.size,
            currentPage = pagerState.currentPage
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (pagerState.currentPage < onboardingPages.lastIndex) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onLoginClick()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            val text =
                if (pagerState.currentPage == onboardingPages.lastIndex)
                    "Войти"
                else
                    "Далее"

            Text(text)
        }
    }
}

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        repeat(pageCount) { index ->

            val width by animateDpAsState(
                targetValue = if (index == currentPage) 26.dp else 10.dp
            )

            val color by animateColorAsState(
                targetValue =
                    if (index == currentPage)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
            )

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .height(10.dp)
                    .width(width)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    CatTheme {
        OnboardingScreen { }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: DrawableResource
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Добро пожаловать",
        description = "Изучайте новые навыки и проходите курсы от лучших преподавателей.",
        imageRes = Res.drawable.stepik_logotype_blac
    ),
    OnboardingPage(
        title = "Тысячи курсов",
        description = "Используйте поиск и находите курсы по интересам.",
        imageRes = Res.drawable.stepik_logotype_blac
    ),
    OnboardingPage(
        title = "Учитесь в своём темпе",
        description = "Сохраняйте курсы и возвращайтесь к ним в любое время.",
        imageRes = Res.drawable.stepik_logotype_blac
    ),
    OnboardingPage(
        title = "Начнём",
        description = "Войдите через Stepik чтобы получить доступ к своим курсам.",
        imageRes = Res.drawable.stepik_logotype_blac
    )
)
