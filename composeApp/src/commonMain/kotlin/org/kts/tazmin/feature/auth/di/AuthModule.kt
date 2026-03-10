package org.kts.tazmin.feature.auth.di

import org.koin.core.module.dsl.viewModel
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

val authModule = module {
    // HTTP Client (без авторизации)
    single { HttpClientFactory.create() }
    // AuthApi
    single<AuthApi> { AuthApi(get()) }
    // Repository
    single<LoginRepository> { LoginRepositoryImpl() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    // UseCase
    factory { LoginUseCase(get()) }
    //Две ViewModel
    viewModel { LoginViewModel(get()) }
    viewModel { OAuthViewModel(get()) }
}
