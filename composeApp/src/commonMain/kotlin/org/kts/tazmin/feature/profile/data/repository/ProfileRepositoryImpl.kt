package org.kts.tazmin.feature.profile.data.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.kts.tazmin.core.common.AppError
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.core.common.runCatchingCancellable
import org.kts.tazmin.feature.profile.data.local.ProfileDao
import org.kts.tazmin.feature.profile.data.mapper.ProfileDbMapper
import org.kts.tazmin.feature.profile.data.mapper.ProfileMapper
import org.kts.tazmin.feature.profile.data.network.ProfileApi
import org.kts.tazmin.feature.profile.domain.model.Profile
import org.kts.tazmin.feature.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi,
    private val profileDao: ProfileDao,
    private val profileDbMapper: ProfileDbMapper,
    private val profileMapper: ProfileMapper
) : ProfileRepository {

    override fun getCurrentUser(): Flow<Resource<Profile>> = flow {

        // сначала пробуем кэш
        val cachedUser = profileDao.getProfile()

        if (cachedUser != null) {
            emit(
                Resource.Success(
                    data = profileDbMapper.toDomain(cachedUser),
                    source = Source.CACHE
                )
            )
        } else {
            emit(Resource.Loading)
        }

        // пробуем сеть
        runCatchingCancellable {
            Napier.d("Fetching user from API...", tag = "ProfileRepository")
            val response = profileApi.getProfile()
            val userDto = response.profilesDto.firstOrNull()
                ?: throw Exception("Список пользователей пуст") //todo

            val user = profileMapper.mapToDomain(userDto)

            // сохраняем в БД
            profileDao.insert(profileDbMapper.toEntity(user))
            Napier.i("User updated from network", tag = "ProfileRepository")
            emit(
                Resource.Success(
                    data = user,
                    source = Source.REMOTE
                )
            )
        }.onFailure { e ->
            if (cachedUser != null) {
                emit(
                    Resource.Error(
                        message = e as AppError,
                        data = profileDbMapper.toDomain(cachedUser)
                    )
                )
            }
        }
    }
}
