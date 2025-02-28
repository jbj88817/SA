package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.RepositoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val repository: RepositoryRepository
) {
    operator fun invoke(organization: String, repositoryName: String): Flow<Result<Repository>> {
        return repository.getRepository(organization, repositoryName)
    }
} 