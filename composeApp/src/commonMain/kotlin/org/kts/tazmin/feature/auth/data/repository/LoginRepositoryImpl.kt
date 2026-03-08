package org.kts.tazmin.feature.auth.data.repository

import org.kts.tazmin.feature.auth.domain.repository.LoginRepository

class LoginRepositoryImpl: LoginRepository {
    override suspend fun login(username: String, password: String): Result<Unit> {
        // мок
        return if(username == "admin" && password == "123456"){
            Result.success(Unit)
        } else{
            Result.failure(IllegalArgumentException("Неверные данные"))
        }
    }
}
