package org.kts.tazmin.feature.profile.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.core.common.Resource
import org.kts.tazmin.feature.profile.domain.model.Profile

interface ProfileRepository {
    fun getCurrentUser(): Flow<Resource<Profile>>
}
