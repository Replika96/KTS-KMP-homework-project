package org.kts.tazmin.feature.courses.di

import org.koin.dsl.module
import org.kts.tazmin.feature.courses.data.repository.CoursesRepositoryImpl
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import org.kts.tazmin.feature.courses.domain.usacase.GetCoursesUseCase
import org.kts.tazmin.feature.courses.presentation.viewmodel.CoursesViewModel

val coursesModule = module {
    single<CoursesRepository> { CoursesRepositoryImpl() }

    factory { GetCoursesUseCase(get()) }

    factory { CoursesViewModel(
        getCoursesUseCase = get(),
    ) }
}
