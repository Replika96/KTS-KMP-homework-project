package org.kts.tazmin.feature.course_details.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.feature.course_details.data.mapper.CourseCTAMapper
import org.kts.tazmin.feature.course_details.data.mapper.CourseDomainMapperImpl
import org.kts.tazmin.feature.course_details.data.mapper.CourseHeaderMapper
import org.kts.tazmin.feature.course_details.data.mapper.CourseInfoMapper
import org.kts.tazmin.feature.course_details.data.mapper.CourseStructureMapperImpl
import org.kts.tazmin.feature.course_details.data.network.CourseInfoApi
import org.kts.tazmin.feature.course_details.data.network.CourseModulesApi
import org.kts.tazmin.feature.course_details.data.repository.CourseDetailsRepositoryImpl
import org.kts.tazmin.feature.course_details.data.repository.CourseStructureRepositoryImpl
import org.kts.tazmin.feature.course_details.domain.mapper.CourseDomainMapper
import org.kts.tazmin.feature.course_details.domain.mapper.CourseStructureMapper
import org.kts.tazmin.feature.course_details.domain.repository.CourseDetailsRepository
import org.kts.tazmin.feature.course_details.domain.repository.CourseStructureRepository
import org.kts.tazmin.feature.course_details.domain.usecase.GetCourseCTAUseCase
import org.kts.tazmin.feature.course_details.domain.usecase.GetCourseHeaderUseCase
import org.kts.tazmin.feature.course_details.domain.usecase.GetCourseInfoUseCase
import org.kts.tazmin.feature.course_details.presentation.viewmodel.CourseDetailsViewModel

val courseDetailModule = module {
    single { CourseInfoApi(get(named("authClient"))) }
    single { CourseModulesApi(get(named("authClient"))) }

    factory<CourseDetailsRepository> {
        CourseDetailsRepositoryImpl(
            dao = get(),
            api = get(),
            headerMapper = get(),
            infoMapper = get(),
            ctaMapper = get(),
        )
    }
    factory<CourseStructureRepository> { CourseStructureRepositoryImpl(
        api = get(),
        structureDao = get(),
        mapper = get(),
        domainMapper = get(),
        courseDetailsDao = get()
    ) }
    // Header mapper
    factory { CourseHeaderMapper }
    // CTA mapper
    factory { CourseCTAMapper() }
    // Info mapper
    factory { CourseInfoMapper(get()) }
    // modules mapper
    factory<CourseDomainMapper> { CourseDomainMapperImpl() }
    factory<CourseStructureMapper> { CourseStructureMapperImpl() }
    // UseCase
    factory { GetCourseHeaderUseCase(get()) }
    factory { GetCourseCTAUseCase(get()) }
    factory { GetCourseInfoUseCase(get()) }
    // ViewModel
    viewModel { (courseId: Int) ->
        CourseDetailsViewModel(
            courseDetailsRepository = get(),
            courseStructureRepository = get(),
            courseId = courseId
        )
    }
}
