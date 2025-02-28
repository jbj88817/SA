package com.example.sa.domain.usecase

import com.example.sa.domain.repository.RepositoryRepository
import javax.inject.Inject

class RefreshRepositoriesUseCase @Inject constructor(
    private val repository: RepositoryRepository
) {
    suspend operator fun invoke(organization: String) {
        repository.refreshRepositories(organization)
    }
} 