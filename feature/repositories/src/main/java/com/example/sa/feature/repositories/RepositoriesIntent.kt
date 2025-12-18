package com.example.sa.feature.repositories

import com.example.sa.domain.model.RepositoryFilter
import com.example.sa.domain.model.RepositorySort

/**
 * Represents user intents/actions for the Repositories screen
 */
sealed class RepositoriesIntent {
    object LoadRepositories : RepositoriesIntent()
    data class UpdateSearchQuery(val query: String) : RepositoriesIntent()
    object ClearSearch : RepositoriesIntent()
    data class UpdateSortOption(val option: RepositorySort) : RepositoriesIntent()
    data class UpdateFilterOption(val option: RepositoryFilter) : RepositoriesIntent()
    object ResetFilters : RepositoriesIntent()
}
