package com.example.intuitrepos.domain.usecase

import com.example.intuitrepos.domain.model.Repository
import com.example.intuitrepos.domain.model.Result
import com.example.intuitrepos.domain.repository.RepositoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val repository: RepositoryRepository
) {
    operator fun invoke(organization: String, repositoryName: String): Flow<Result<Repository>> {
        return repository.getRepository(organization, repositoryName)
    }
} 