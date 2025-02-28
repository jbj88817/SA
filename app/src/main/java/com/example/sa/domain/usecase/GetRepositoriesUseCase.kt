package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.RepositoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRepositoriesUseCase @Inject constructor(
    private val repository: RepositoryRepository
) {
    operator fun invoke(organization: String): Flow<Result<List<Repository>>> {
        return repository.getRepositories(organization)
    }
} 