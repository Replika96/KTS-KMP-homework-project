package org.kts.tazmin.feature.auth.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.core.datastore.UserPreferences
import org.kts.tazmin.core.network.HttpClientFactory
import org.kts.tazmin.core.token.TokenStorage
import org.kts.tazmin.feature.auth.data.network.api.AuthApi
import org.kts.tazmin.feature.auth.data.repository.AuthRepositoryImpl
import org.kts.tazmin.feature.auth.domain.repository.AuthRepository
import org.kts.tazmin.feature.auth.domain.usecase.LoginUseCase
import org.kts.tazmin.feature.auth.presentation.viewmodel.AppStartViewModel
import org.kts.tazmin.feature.auth.presentation.viewmodel.OAuthViewModel
import org.kts.tazmin.feature.profile.data.repository.ProfileRepositoryImpl
import org.kts.tazmin.feature.profile.domain.repository.ProfileRepository

val authModule = module {

    single<TokenStorage> { TokenStorage(get()) }

    single { HttpClientFactory(tokenStorage = get()) }

    // HTTP Client (без авторизации)
    single(named("publicClient")) {
        get<HttpClientFactory>().create()
    }

    // клиент с авторизацией
    single(named("authClient")) {
        get<HttpClientFactory>().createAuthenticated(get())
    }

    // AuthApi
    single<AuthApi> {
        AuthApi(get(named("publicClient")))
    }
    factory { UserPreferences(get()) }

    // Repository
    factory<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    factory<ProfileRepository> {
        ProfileRepositoryImpl(
            userApi = get(),
            userDao = get(),
            userMapper = get()
        )
    }

    // UseCase
    factory { LoginUseCase(get()) }

    //ViewModel
    viewModel { OAuthViewModel(get()) }
    viewModel { AppStartViewModel(get()) }
}
