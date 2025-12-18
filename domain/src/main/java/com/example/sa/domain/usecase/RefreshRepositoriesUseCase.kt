package com.example.sa.domain.usecase

import com.example.sa.domain.repository.RepositoriesRepository
import javax.inject.Inject

class RefreshRepositoriesUseCase @Inject constructor(
    private val repository: RepositoriesRepository
) {
    suspend operator fun invoke(organization: String) {
        repository.refreshRepositories(organization)
    }
}
