package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for getting a single repository by name
 */
class GetRepositoryUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    operator fun invoke(name: String): Flow<Result<Repository>> = flow<Result<Repository>> {
        emit(Result.Success(repository.getRepository(name)))
    }.catch { e ->
        emit(Result.Error(e))
    }
} 