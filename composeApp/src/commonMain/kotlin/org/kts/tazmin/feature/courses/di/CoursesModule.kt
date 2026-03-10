package org.kts.tazmin.feature.courses.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.kts.tazmin.core.network.HttpClientFactory
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.data.repository.CoursesRepositoryImpl
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import org.kts.tazmin.feature.courses.domain.usacase.GetCoursesUseCase
import org.kts.tazmin.feature.courses.domain.usacase.SearchCoursesUseCase
import org.kts.tazmin.feature.courses.presentation.viewmodel.CoursesViewModel

val coursesModule = module {

    // HTTP
    single { HttpClientFactory.create() }

    // API
    single { CoursesApi(get()) }

    // Repository
    single<CoursesRepository> {
        CoursesRepositoryImpl(api = get())
    }

    // UseCases
    factory { GetCoursesUseCase(get()) }
    factory { SearchCoursesUseCase(get()) }

    // ViewModel
    viewModel {
        CoursesViewModel(
            getCoursesUseCase = get(),
            searchCoursesUseCase = get()
        )
    }
}