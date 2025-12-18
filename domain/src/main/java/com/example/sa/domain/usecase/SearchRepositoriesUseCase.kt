package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import javax.inject.Inject

/**
 * Use case for searching repositories by name or description
 */
class SearchRepositoriesUseCase @Inject constructor() {
    
    operator fun invoke(repositories: List<Repository>, query: String): List<Repository> {
        return if (query.isBlank()) {
            repositories
        } else {
            repositories.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description?.contains(query, ignoreCase = true) == true
            }
        }
    }
} 