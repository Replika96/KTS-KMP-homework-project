package org.kts.tazmin.feature.feed.di

import org.koin.dsl.module
import org.kts.tazmin.feature.feed.data.repository.PostsRepositoryImpl
import org.kts.tazmin.feature.feed.domain.repository.PostsRepository
import org.kts.tazmin.feature.feed.domain.usacase.GetPostsUseCase
import org.kts.tazmin.feature.feed.presentation.viewmodel.FeedViewModel

val feedModule = module {
    single<PostsRepository> { PostsRepositoryImpl() }

    factory { GetPostsUseCase(get()) }

    factory { FeedViewModel(
        getPostsUseCase = get(),
    ) }

}
