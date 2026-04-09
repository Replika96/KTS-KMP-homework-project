package org.kts.tazmin.feature.profile.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.kts.tazmin.core.common.Config.baseUrl
import org.kts.tazmin.feature.profile.data.model.ProfileResponse

class ProfileApi(private val client: HttpClient) {

    suspend fun getProfile(): ProfileResponse {
        return client.get("$baseUrl/api/stepics/1").body()
    }
}
