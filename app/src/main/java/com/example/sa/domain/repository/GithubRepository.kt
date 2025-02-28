package com.example.sa.domain.repository

import com.example.sa.domain.model.Repository

interface GithubRepository {
    suspend fun getRepositories(): List<Repository>
    suspend fun getRepository(name: String): Repository
} 