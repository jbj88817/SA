package com.example.sa.data.repository

import com.example.sa.data.remote.GithubApiService
import com.example.sa.domain.model.Repository
import com.example.sa.domain.repository.GithubRepository
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val api: GithubApiService
) : GithubRepository {
    
    private val organization = "intuit"
    
    override suspend fun getRepositories(): List<Repository> {
        return api.getRepositories(organization).map { it.toDomainModel() }
    }
    
    override suspend fun getRepository(name: String): Repository {
        return api.getRepository(organization, name).toDomainModel()
    }
} 