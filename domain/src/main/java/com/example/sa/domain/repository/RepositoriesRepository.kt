package com.example.sa.domain.repository

import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface RepositoriesRepository {
    fun getRepositories(organization: String): Flow<Result<List<Repository>>>
    fun getRepository(organization: String, repositoryName: String): Flow<Result<Repository>>
    suspend fun refreshRepositories(organization: String)
    suspend fun refreshRepository(organization: String, repositoryName: String)
}
