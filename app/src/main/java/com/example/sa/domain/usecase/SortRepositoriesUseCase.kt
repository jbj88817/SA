package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.ui.screens.repositories.SortOption
import javax.inject.Inject

/**
 * Use case for sorting repositories by different criteria
 */
class SortRepositoriesUseCase @Inject constructor() {
    
    operator fun invoke(repositories: List<Repository>, sortOption: SortOption): List<Repository> {
        return when (sortOption) {
            SortOption.NAME_ASC -> repositories.sortedBy { it.name }
            SortOption.NAME_DESC -> repositories.sortedByDescending { it.name }
            SortOption.STARS_ASC -> repositories.sortedBy { it.stars }
            SortOption.STARS_DESC -> repositories.sortedByDescending { it.stars }
            SortOption.UPDATED_ASC -> repositories.sortedBy { it.updatedAt }
            SortOption.UPDATED_DESC -> repositories.sortedByDescending { it.updatedAt }
        }
    }
} 