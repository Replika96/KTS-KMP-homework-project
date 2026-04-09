package org.kts.tazmin.feature.profile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    @SerialName("users")
    val profilesDto: List<ProfileDto>
)

@Serializable
data class ProfileDto(

    val id: Int,

    val profile: String? = null,

    @SerialName("is_private")
    val isPrivate: Boolean = false,

    @SerialName("short_bio")
    val shortBio: String? = null,

    val details: String? = null,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("full_name")
    val fullName: String? = null,

    val avatar: String? = null,

    val city: String? = null,

    val knowledge: Int = 0,

    @SerialName("knowledge_rank")
    val knowledgeRank: Int? = 0,

    val reputation: Int = 0,

    @SerialName("reputation_rank")
    val reputationRank: Int? = 0,

    @SerialName("join_date")
    val joinDate: String? = null,

    @SerialName("social_profiles")
    val socialProfiles: List<String> = emptyList(),

    @SerialName("solved_steps_count")
    val solvedStepsCount: Int? = 0,

    @SerialName("followers_count")
    val followersCount: Int = 0
)
