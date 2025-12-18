package com.example.sa.feature.repositories

import com.example.sa.domain.model.RepositoryFilter
import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.RepositorySort

/**
 * Represents the UI state for the Repositories screen
 */
data class RepositoriesState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val repositories: List<Repository> = emptyList(),
    val filteredRepositories: List<Repository> = emptyList(),
    val searchQuery: String = "",
    val sortOption: RepositorySort = RepositorySort.NAME_ASC,
    val filterOption: RepositoryFilter = RepositoryFilter.ALL,
    val error: String? = null,
    val refreshError: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && !isRefreshing && filteredRepositories.isEmpty() && error == null
}
