package org.kts.tazmin.feature.profile.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.kts.tazmin.core.common.Config.baseUrl
import org.kts.tazmin.feature.profile.data.model.UserResponse

class UserApi(private val client: HttpClient) {

    suspend fun getUser(): UserResponse {
        return client.get("$baseUrl/api/stepics/1").body()
    }
}