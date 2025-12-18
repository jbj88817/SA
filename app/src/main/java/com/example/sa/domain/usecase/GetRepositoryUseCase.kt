package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.RepositoriesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting a single repository by name
 */
class GetRepositoryUseCase @Inject constructor(
    private val repository: RepositoriesRepository
) {
    operator fun invoke(organization: String, name: String): Flow<Result<Repository>> {
        return repository.getRepository(organization, name)
    }
}
