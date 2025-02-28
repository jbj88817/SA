package com.example.sa.ui.screens.repositories

import com.example.sa.domain.model.Repository

/**
 * Represents the UI state for the Repositories screen
 */
data class RepositoriesState(
    val isLoading: Boolean = false,
    val repositories: List<Repository> = emptyList(),
    val filteredRepositories: List<Repository> = emptyList(),
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NAME_ASC,
    val filterOption: FilterOption = FilterOption.ALL,
    val error: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && filteredRepositories.isEmpty() && error == null
} 