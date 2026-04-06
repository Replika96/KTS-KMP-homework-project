package org.kts.tazmin.feature.profile.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.feature.profile.data.mapper.UserMapper
import org.kts.tazmin.feature.profile.data.remote.UserApi
import org.kts.tazmin.feature.profile.data.repository.ProfileRepositoryImpl
import org.kts.tazmin.feature.profile.domain.repository.ProfileRepository
import org.kts.tazmin.feature.profile.domain.usecase.GetUserUseCase
import org.kts.tazmin.feature.profile.domain.usecase.LogoutUseCase
import org.kts.tazmin.feature.profile.presentation.viewmodel.ProfileViewModel

val profileModule = module {

    single { UserApi(get(named("authClient"))) }

    factory<ProfileRepository> {
        ProfileRepositoryImpl(
            userApi = get(),
            userDao = get(),
            userMapper = get()
        )
    }

    single<UserMapper> { UserMapper }

    viewModel {
        ProfileViewModel(
            getUserUseCase = get(),
            logoutUseCase = get()
        )
    }

    factory { GetUserUseCase(get()) }

    factory {
        LogoutUseCase(
            authRepository = get(),
            tokenStorage = get()
        )
    }
}