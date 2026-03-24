package org.kts.tazmin.feature.courses.di

import org.kts.tazmin.feature.courses.presentation.viewmodel.SearchCoursesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.feature.courses.data.mapper.CatalogMapper
import org.kts.tazmin.feature.courses.data.mapper.CourseMapper
import org.kts.tazmin.feature.courses.data.mapper.MyCourseMapper
import org.kts.tazmin.feature.courses.data.network.api.CatalogApi
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.data.repository.CatalogRepositoryImpl
import org.kts.tazmin.feature.courses.data.repository.CoursesRepositoryImpl
import org.kts.tazmin.feature.courses.data.repository.MyCoursesRepositoryImpl
import org.kts.tazmin.feature.courses.domain.repository.CatalogRepository
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import org.kts.tazmin.feature.courses.domain.repository.MyCoursesRepository
import org.kts.tazmin.feature.courses.domain.usecase.GetCatalogUseCase
import org.kts.tazmin.feature.courses.domain.usecase.GetCoursesUseCase
import org.kts.tazmin.feature.courses.domain.usecase.GetMyCoursesUseCase
import org.kts.tazmin.feature.courses.domain.usecase.SearchCoursesUseCase
import org.kts.tazmin.feature.courses.presentation.viewmodel.CatalogViewModel
import org.kts.tazmin.feature.courses.presentation.viewmodel.MyCoursesViewModel

val coursesModule = module {
    // API
    single {
        CoursesApi(get(named("authClient")))
    }
    single {
        CatalogApi(get(named("authClient")))
    }
    // Repository
    factory<CoursesRepository> {
        CoursesRepositoryImpl(
            coursesApi = get(),
            courseDao = get(),
            courseMapper = get()
        )
    }
    factory<MyCoursesRepository> {
        MyCoursesRepositoryImpl(
            coursesApi = get(),
            myCoursesDao = get(),
            mapper = get()
        )
    }
    factory<CatalogRepository> {
        CatalogRepositoryImpl(
            catalogApi = get(),
            catalogDao = get(),
            courseDao = get(),
            courseMapper = get(),
            catalogMapper = get()
        )
    }
    single<MyCourseMapper> { MyCourseMapper }
    single<CourseMapper> { CourseMapper }
    single<CatalogMapper> { CatalogMapper }
    // UseCases
    factory { GetCoursesUseCase(get()) }
    factory { SearchCoursesUseCase(get()) }
    factory { GetMyCoursesUseCase(get()) }
    factory { GetCatalogUseCase(get()) }
    // ViewModel
    viewModel {
        CatalogViewModel(
            getCatalogUseCase = get()
        )
    }
    viewModel {
        SearchCoursesViewModel(
            searchCoursesUseCase = get()
        )
    }
    viewModel {
        MyCoursesViewModel(
            getMyCoursesUseCase = get()
        )
    }
}
