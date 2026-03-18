package org.kts.tazmin.feature.profile.data.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.core.common.Source
import org.kts.tazmin.feature.profile.data.local.UserDao
import org.kts.tazmin.feature.profile.data.mapper.UserDbMapper.toDomain
import org.kts.tazmin.feature.profile.data.mapper.UserDbMapper.toEntity
import org.kts.tazmin.feature.profile.data.mapper.UserMapper
import org.kts.tazmin.feature.profile.data.remote.UserApi
import org.kts.tazmin.feature.profile.domain.model.User
import org.kts.tazmin.feature.profile.domain.repository.ProfileRepository
import kotlin.coroutines.cancellation.CancellationException

class ProfileRepositoryImpl(
    private val userApi: UserApi,
    private val userDao: UserDao,
    private val userMapper: UserMapper
) : ProfileRepository {

    override fun getCurrentUser(): Flow<Resource<User>> = flow {

        // сначала пробуем кэш
        val cachedUser = userDao.getUser()

        if (cachedUser != null) {
            emit(
                Resource.Success(
                    data = cachedUser.toDomain(),
                    source = Source.CACHE
                )
            )
        } else {
            emit(Resource.Loading)
        }

        // пробуем сеть
        try {
            val response = userApi.getUser()
            val userDto = response.users.firstOrNull()
                ?: throw IllegalStateException("Список пользователей пуст")

            val user = userMapper.mapToDomain(userDto)

            // сохраняем в БД
            userDao.insert(user.toEntity())

            emit(
                Resource.Success(
                    data = user,
                    source = Source.REMOTE
                )
            )

        } catch (e: Exception) {

            if (e is CancellationException) throw e

            Napier.e("getCurrentUser error", e)

            // если есть кэш просто ошибка поверх него
            if (cachedUser != null) {
                emit(
                    Resource.Error(
                        message = "Не удалось обновить данные",
                        data = cachedUser.toDomain()
                    )
                )
            } else {
                emit(
                    Resource.Error(
                        message = e.message ?: "Ошибка загрузки"
                    )
                )
            }
        }
    }
}
