package org.kts.tazmin.feature.auth.di

import org.koin.dsl.module
import org.kts.tazmin.feature.auth.data.repository.LoginRepositoryImpl
import org.kts.tazmin.feature.auth.domain.repository.LoginRepository
import org.kts.tazmin.feature.auth.domain.usecase.LoginUseCase
import org.kts.tazmin.feature.auth.presentation.viewmodel.LoginViewModel

val authModule = module {
    // Repository
    single<LoginRepository> { LoginRepositoryImpl() }

    // UseCase
    factory { LoginUseCase(get()) }

    // ViewModel
    factory { LoginViewModel(loginUseCase = get()) }

}
