package org.kts.tazmin.feature.auth.presentation.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.close
import ktskotlinproject.composeapp.generated.resources.login_with_stepik
import ktskotlinproject.composeapp.generated.resources.next
import ktskotlinproject.composeapp.generated.resources.onboarding_desc_courses
import ktskotlinproject.composeapp.generated.resources.onboarding_desc_pace
import ktskotlinproject.composeapp.generated.resources.onboarding_desc_start
import ktskotlinproject.composeapp.generated.resources.onboarding_desc_welcome
import ktskotlinproject.composeapp.generated.resources.onboarding_title_courses
import ktskotlinproject.composeapp.generated.resources.onboarding_title_pace
import ktskotlinproject.composeapp.generated.resources.onboarding_title_start
import ktskotlinproject.composeapp.generated.resources.onboarding_title_welcome
import ktskotlinproject.composeapp.generated.resources.stepik_logotype_blac
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.kts.tazmin.feature.auth.presentation.viewmodel.OAuthViewModel
import org.kts.tazmin.theme.CatTheme
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    oAuthViewModel: OAuthViewModel = koinViewModel<OAuthViewModel>(),
    onNavigateToMain: () -> Unit
) {
    val oAuthState by oAuthViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(oAuthState.isAuthenticated) {
        if (oAuthState.isAuthenticated) {
            onNavigateToMain()
        }
    }

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
                    text = stringResource(item.title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(item.description),
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

        val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

        Button(
            onClick = {
                if (!isLastPage) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    oAuthViewModel.onLoginWithStepik()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLastPage) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isLastPage) stringResource(Res.string.login_with_stepik) else stringResource(
                            Res.string.next
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
    if (oAuthState.showWebView) {
        LoginWebView(
            onCodeReceived = oAuthViewModel::onCodeReceived,
            onError = oAuthViewModel::onError,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = { oAuthViewModel.resetWebView() },
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = stringResource(Res.string.close))
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
        OnboardingScreen(
            onNavigateToMain = { }
        )
    }
}

data class OnboardingPage(
    val title: StringResource,
    val description: StringResource,
    val imageRes: DrawableResource
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = Res.string.onboarding_title_welcome,
        description = Res.string.onboarding_desc_welcome,
        imageRes = Res.drawable.stepik_logotype_blac
    ),
    OnboardingPage(
        title = Res.string.onboarding_title_courses,
        description = Res.string.onboarding_desc_courses,
        imageRes = Res.drawable.stepik_logotype_blac
    ),
    OnboardingPage(
        title = Res.string.onboarding_title_pace,
        description = Res.string.onboarding_desc_pace,
        imageRes = Res.drawable.stepik_logotype_blac
    ),
    OnboardingPage(
        title = Res.string.onboarding_title_start,
        description = Res.string.onboarding_desc_start,
        imageRes = Res.drawable.stepik_logotype_blac
    )
)
