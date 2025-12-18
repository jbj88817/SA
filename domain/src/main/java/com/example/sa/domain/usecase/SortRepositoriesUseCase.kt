package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.RepositorySort
import javax.inject.Inject

/**
 * Use case for sorting repositories by different criteria
 */
class SortRepositoriesUseCase @Inject constructor() {
    
    operator fun invoke(repositories: List<Repository>, sort: RepositorySort): List<Repository> {
        return when (sort) {
            RepositorySort.NAME_ASC -> repositories.sortedBy { it.name }
            RepositorySort.NAME_DESC -> repositories.sortedByDescending { it.name }
            RepositorySort.STARS_ASC -> repositories.sortedBy { it.stars }
            RepositorySort.STARS_DESC -> repositories.sortedByDescending { it.stars }
            RepositorySort.UPDATED_ASC -> repositories.sortedBy { it.updatedAt }
            RepositorySort.UPDATED_DESC -> repositories.sortedByDescending { it.updatedAt }
        }
    }
}
