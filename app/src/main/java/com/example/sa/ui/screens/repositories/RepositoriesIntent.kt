package com.example.sa.ui.screens.repositories

/**
 * Represents user intents/actions for the Repositories screen
 */
sealed class RepositoriesIntent {
    object LoadRepositories : RepositoriesIntent()
    data class UpdateSearchQuery(val query: String) : RepositoriesIntent()
    object ClearSearch : RepositoriesIntent()
    data class UpdateSortOption(val option: SortOption) : RepositoriesIntent()
    data class UpdateFilterOption(val option: FilterOption) : RepositoriesIntent()
    object ResetFilters : RepositoriesIntent()
} 