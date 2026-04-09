package org.kts.tazmin.feature.profile.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.feature.profile.data.mapper.ProfileDbMapper
import org.kts.tazmin.feature.profile.data.mapper.ProfileMapper
import org.kts.tazmin.feature.profile.data.mapper.UserDbMapper
import org.kts.tazmin.feature.profile.data.network.ProfileApi
import org.kts.tazmin.feature.profile.data.repository.ProfileRepositoryImpl
import org.kts.tazmin.feature.profile.domain.repository.ProfileRepository
import org.kts.tazmin.feature.profile.domain.usecase.GetUserUseCase
import org.kts.tazmin.feature.profile.domain.usecase.LogoutUseCase
import org.kts.tazmin.feature.profile.presentation.viewmodel.ProfileViewModel

val profileModule = module {

    single { ProfileApi(get(named("authClient"))) }

    factory<ProfileRepository> {
        ProfileRepositoryImpl(
            profileApi = get(),
            profileDao = get(),
            profileDbMapper = get(),
            profileMapper = get()
        )
    }

    factory<UserDbMapper> { UserDbMapper() }

    factory<ProfileMapper> { ProfileMapper() }
    factory<ProfileDbMapper> { ProfileDbMapper() }

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
