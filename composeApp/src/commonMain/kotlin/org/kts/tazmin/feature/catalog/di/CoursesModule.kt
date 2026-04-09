package org.kts.tazmin.feature.catalog.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.feature.catalog.data.mapper.CatalogMapperImpl
import org.kts.tazmin.feature.catalog.data.mapper.CourseMapperImpl
import org.kts.tazmin.feature.catalog.data.mapper.MyCourseMapperImpl
import org.kts.tazmin.feature.catalog.data.network.api.CatalogApiImpl
import org.kts.tazmin.feature.catalog.data.network.api.CoursesApiImpl
import org.kts.tazmin.feature.catalog.data.repository.CatalogRepositoryImpl
import org.kts.tazmin.feature.catalog.data.repository.CoursesListRepositoryImpl
import org.kts.tazmin.feature.catalog.data.repository.CoursesRepositoryImpl
import org.kts.tazmin.feature.catalog.data.repository.MyCoursesRepositoryImpl
import org.kts.tazmin.feature.catalog.domain.mapper.CatalogMapper
import org.kts.tazmin.feature.catalog.domain.mapper.CourseMapper
import org.kts.tazmin.feature.catalog.domain.mapper.MyCourseMapper
import org.kts.tazmin.feature.catalog.domain.network.CatalogApi
import org.kts.tazmin.feature.catalog.domain.network.CoursesApi
import org.kts.tazmin.feature.catalog.domain.repository.CatalogRepository
import org.kts.tazmin.feature.catalog.domain.repository.CoursesListRepository
import org.kts.tazmin.feature.catalog.domain.repository.CoursesRepository
import org.kts.tazmin.feature.catalog.domain.repository.MyCoursesRepository
import org.kts.tazmin.feature.catalog.domain.usecase.GetCatalogUseCase
import org.kts.tazmin.feature.catalog.domain.usecase.GetCoursesUseCase
import org.kts.tazmin.feature.catalog.domain.usecase.GetMyCoursesUseCase
import org.kts.tazmin.feature.catalog.domain.usecase.SearchCoursesUseCase
import org.kts.tazmin.feature.catalog.presentation.viewmodel.CatalogViewModel
import org.kts.tazmin.feature.catalog.presentation.viewmodel.MyCoursesViewModel
import org.kts.tazmin.feature.catalog.presentation.viewmodel.SearchCoursesViewModel

val coursesModule = module {
    // API
    factory<CoursesApi> {
        CoursesApiImpl(get(named("authClient")))
    }
    factory<CatalogApi> {
        CatalogApiImpl(get(named("authClient")))
    }
    // Repository
    factory<CoursesRepository> {
        CoursesRepositoryImpl(
            coursesApi = get(),
            courseDao = get(),
            courseMapper = get()
        )
    }
    factory<CoursesListRepository> {
        CoursesListRepositoryImpl(
            courseListDao = get(),
            catalogApi = get(),
            courseMapper = get(),
            courseDao = get()
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
            catalogMapper = get(),
        )
    }
    factory<MyCourseMapper> { MyCourseMapperImpl() }
    factory<CourseMapper> { CourseMapperImpl() }
    factory<CatalogMapper> { CatalogMapperImpl() }
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
