package org.kts.tazmin.feature.profile.data.repository

import coil3.network.HttpException
import io.github.aakira.napier.Napier
import kotlinx.io.IOException
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

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        val response = userApi.getUser()
        val userDto = response.users.firstOrNull()
            ?: throw IllegalStateException("Список пользователей пуст")

        val user = userMapper.mapToDomain(userDto)


        userDao.insert(user.toEntity())
        Napier.d("getCurrentUser: сохранено в кеш")
        user
    }.recoverCatching { e ->
        if (e is CancellationException) throw e
        Napier.e("getCurrentUser ошибка", e)
        if (e is IOException || e is HttpException) {
            val cached = userDao.getUser()
            if (cached != null) {
                Napier.d("getCurrentUser: кэш найден")
                return@recoverCatching cached.toDomain()
            } else{
                Napier.d("getCurrentUser: кэша нет")
            }
        }
        throw e
    }
}
