package com.example.sa.ui.screens.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sa.R
import com.example.sa.domain.model.Repository
import com.example.sa.domain.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepositoriesUiState>(RepositoriesUiState.Loading)
    val uiState: StateFlow<RepositoriesUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOption = MutableStateFlow(SortOption.NAME_ASC)
    val sortOption: StateFlow<SortOption> = _sortOption

    private val _filterOption = MutableStateFlow(FilterOption.ALL)
    val filterOption: StateFlow<FilterOption> = _filterOption

    private val _repositories = MutableStateFlow<List<Repository>>(emptyList())

    init {
        loadRepositories()
        
        // Combine search, sort, and filter to update UI state
        combine(
            _repositories,
            _searchQuery,
            _sortOption,
            _filterOption
        ) { repos, query, sort, filter ->
            applySearchSortAndFilter(repos, query, sort, filter)
        }.onEach { filteredAndSortedRepos ->
            updateUiState(filteredAndSortedRepos)
        }.launchIn(viewModelScope)
    }

    fun loadRepositories() {
        viewModelScope.launch {
            _uiState.value = RepositoriesUiState.Loading
            try {
                val repos = repository.getRepositories()
                _repositories.value = repos
                // UI state will be updated by the combine flow
            } catch (e: Exception) {
                _uiState.value = RepositoriesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun updateFilterOption(option: FilterOption) {
        _filterOption.value = option
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _sortOption.value = SortOption.NAME_ASC
        _filterOption.value = FilterOption.ALL
    }

    private fun applySearchSortAndFilter(
        repositories: List<Repository>,
        query: String,
        sort: SortOption,
        filter: FilterOption
    ): List<Repository> {
        // Apply search
        val searchResults = if (query.isBlank()) {
            repositories
        } else {
            repositories.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description?.contains(query, ignoreCase = true) == true
            }
        }

        // Apply filter
        val filteredResults = when (filter) {
            FilterOption.ALL -> searchResults
            FilterOption.JAVA -> searchResults.filter { 
                it.language?.equals("Java", ignoreCase = true) == true 
            }
            FilterOption.KOTLIN -> searchResults.filter { 
                it.language?.equals("Kotlin", ignoreCase = true) == true 
            }
            FilterOption.ANDROID -> searchResults.filter { 
                it.topics.any { topic -> 
                    topic.equals("android", ignoreCase = true) 
                }
            }
        }

        // Apply sort
        return when (sort) {
            SortOption.NAME_ASC -> filteredResults.sortedBy { it.name }
            SortOption.NAME_DESC -> filteredResults.sortedByDescending { it.name }
            SortOption.STARS_ASC -> filteredResults.sortedBy { it.stars }
            SortOption.STARS_DESC -> filteredResults.sortedByDescending { it.stars }
            SortOption.UPDATED_ASC -> filteredResults.sortedBy { it.updatedAt }
            SortOption.UPDATED_DESC -> filteredResults.sortedByDescending { it.updatedAt }
        }
    }

    private fun updateUiState(repositories: List<Repository>) {
        _uiState.value = if (repositories.isEmpty()) {
            RepositoriesUiState.Empty
        } else {
            RepositoriesUiState.Success(repositories)
        }
    }
}

enum class SortOption(val stringResId: Int) {
    NAME_ASC(R.string.sort_name_asc),
    NAME_DESC(R.string.sort_name_desc),
    STARS_ASC(R.string.sort_stars_asc),
    STARS_DESC(R.string.sort_stars_desc),
    UPDATED_ASC(R.string.sort_updated_asc),
    UPDATED_DESC(R.string.sort_updated_desc)
}

enum class FilterOption(val stringResId: Int) {
    ALL(R.string.filter_all),
    JAVA(R.string.filter_java),
    KOTLIN(R.string.filter_kotlin),
    ANDROID(R.string.filter_android)
}

sealed class RepositoriesUiState {
    object Loading : RepositoriesUiState()
    data class Success(val repositories: List<Repository>) : RepositoriesUiState()
    data class Error(val message: String) : RepositoriesUiState()
    object Empty : RepositoriesUiState()
} 