package org.kts.tazmin.feature.courses.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.data.repository.CoursesRepositoryImpl
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import org.kts.tazmin.feature.courses.domain.usacase.GetCoursesUseCase
import org.kts.tazmin.feature.courses.domain.usacase.SearchCoursesUseCase
import org.kts.tazmin.feature.courses.presentation.viewmodel.CoursesViewModel

val coursesModule = module {
    // API
    single {
        CoursesApi(get(named("authClient")))
    }
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
