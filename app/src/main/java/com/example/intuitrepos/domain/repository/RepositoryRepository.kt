package com.example.intuitrepos.domain.repository

import com.example.intuitrepos.domain.model.Repository
import com.example.intuitrepos.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface RepositoryRepository {
    fun getRepositories(organization: String): Flow<Result<List<Repository>>>
    fun getRepository(organization: String, repositoryName: String): Flow<Result<Repository>>
    suspend fun refreshRepositories(organization: String)
} 