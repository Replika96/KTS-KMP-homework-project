package org.kts.tazmin.feature.auth.domain.repository

interface LoginRepository {
    suspend fun login(
        username: String,
        password: String): Result<Unit>
}
