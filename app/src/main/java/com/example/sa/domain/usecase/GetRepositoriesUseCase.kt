package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for getting all repositories
 */
class GetRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    operator fun invoke(): Flow<Result<List<Repository>>> = flow<Result<List<Repository>>> {
        emit(Result.Success(repository.getRepositories()))
    }.catch { e ->
        emit(Result.Error(e))
    }
} 