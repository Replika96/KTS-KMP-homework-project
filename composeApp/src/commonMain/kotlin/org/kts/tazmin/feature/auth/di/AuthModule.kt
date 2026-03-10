package org.kts.tazmin.feature.auth.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.core.network.HttpClientFactory
import org.kts.tazmin.feature.auth.data.remote.AuthApi
import org.kts.tazmin.feature.auth.data.repository.AuthRepositoryImpl
import org.kts.tazmin.feature.auth.data.repository.LoginRepositoryImpl
import org.kts.tazmin.feature.auth.domain.repository.AuthRepository
import org.kts.tazmin.feature.auth.domain.repository.LoginRepository
import org.kts.tazmin.feature.auth.domain.usecase.LoginUseCase
import org.kts.tazmin.feature.auth.presentation.viewmodel.LoginViewModel
import org.kts.tazmin.feature.auth.presentation.viewmodel.OAuthViewModel
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi

val authModule = module {
    // HTTP Client (без авторизации)
    single(named("publicClient")) {
        HttpClientFactory.create()
    }
    // клиент с авторизацией
    single(named("authClient")) {
        HttpClientFactory.createAuthenticated(get())
    }
    // AuthApi
    single<AuthApi> {
        AuthApi(get(named("publicClient")))
    }
    // Courses API
    single<CoursesApi> {
        CoursesApi(get(named("authClient")))
    }
    // Repository
    single<LoginRepository> { LoginRepositoryImpl() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    // UseCase
    factory { LoginUseCase(get()) }
    //Две ViewModel
    viewModel { LoginViewModel(get()) }
    viewModel { OAuthViewModel(get()) }
}
