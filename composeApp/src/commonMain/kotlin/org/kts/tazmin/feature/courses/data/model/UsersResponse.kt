package org.kts.tazmin.feature.courses.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    @SerialName("meta")
    val meta: Meta,

    @SerialName("users")
    val users: List<UserDto>
)

@Serializable
data class UserDto(
    val id: Int,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("avatar")
    val avatar: String? = null,

    @SerialName("is_private")
    val isPrivate: Boolean = false
)
