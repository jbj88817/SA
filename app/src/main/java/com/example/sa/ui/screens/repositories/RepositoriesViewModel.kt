package com.example.sa.ui.screens.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sa.BuildConfig
import com.example.sa.R
import com.example.sa.domain.usecase.FilterRepositoriesUseCase
import com.example.sa.domain.usecase.GetRepositoriesUseCase
import com.example.sa.domain.usecase.RefreshRepositoriesUseCase
import com.example.sa.domain.usecase.SearchRepositoriesUseCase
import com.example.sa.domain.usecase.SortRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    private val refreshRepositoriesUseCase: RefreshRepositoriesUseCase,
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val filterRepositoriesUseCase: FilterRepositoriesUseCase,
    private val sortRepositoriesUseCase: SortRepositoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoriesState(isLoading = true))
    val uiState: StateFlow<RepositoriesState> = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow<RepositoriesIntent>()

    private var observeJob: Job? = null
    private var refreshJob: Job? = null
    
    init {
        processIntents()
        processIntent(RepositoriesIntent.LoadRepositories)
    }
    
    fun processIntent(intent: RepositoriesIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }
    
    private fun processIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                when (intent) {
                    is RepositoriesIntent.LoadRepositories -> loadRepositories()
                    is RepositoriesIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
                    is RepositoriesIntent.ClearSearch -> clearSearch()
                    is RepositoriesIntent.UpdateSortOption -> updateSortOption(intent.option)
                    is RepositoriesIntent.UpdateFilterOption -> updateFilterOption(intent.option)
                    is RepositoriesIntent.ResetFilters -> resetFilters()
                }
            }
        }
    }

    private fun loadRepositories() {
        startObservingRepositories()
        refreshRepositories()
    }

    private fun startObservingRepositories() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            getRepositoriesUseCase(BuildConfig.GITHUB_ORG).collectLatest { result ->
                result.fold(
                    onSuccess = { repositories ->
                        _uiState.update { currentState ->
                            val filteredAndSorted = applyFiltersAndSort(
                                repositories,
                                currentState.searchQuery,
                                currentState.filterOption,
                                currentState.sortOption
                            )
                            currentState.copy(
                                isLoading = false,
                                repositories = repositories,
                                filteredRepositories = filteredAndSorted,
                                error = null
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update { currentState ->
                            val message = throwable.message ?: "Unknown error"
                            if (currentState.repositories.isEmpty()) {
                                currentState.copy(isLoading = false, error = message)
                            } else {
                                currentState.copy(isLoading = false, refreshError = message)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun refreshRepositories() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = currentState.repositories.isEmpty(),
                    isRefreshing = true,
                    error = null,
                    refreshError = null
                )
            }

            try {
                refreshRepositoriesUseCase(BuildConfig.GITHUB_ORG)
            } catch (t: Throwable) {
                _uiState.update { currentState ->
                    val message = t.message ?: "Unknown error"
                    if (currentState.repositories.isEmpty()) {
                        currentState.copy(error = message)
                    } else {
                        currentState.copy(refreshError = message)
                    }
                }
            } finally {
                _uiState.update { it.copy(isRefreshing = false, isLoading = false) }
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            val filteredAndSorted = applyFiltersAndSort(
                currentState.repositories,
                query,
                currentState.filterOption,
                currentState.sortOption
            )
            currentState.copy(
                searchQuery = query,
                filteredRepositories = filteredAndSorted
            )
        }
    }

    private fun clearSearch() {
        updateSearchQuery("")
    }

    private fun updateSortOption(option: SortOption) {
        _uiState.update { currentState ->
            val filteredAndSorted = applyFiltersAndSort(
                currentState.repositories,
                currentState.searchQuery,
                currentState.filterOption,
                option
            )
            currentState.copy(
                sortOption = option,
                filteredRepositories = filteredAndSorted
            )
        }
    }

    private fun updateFilterOption(option: FilterOption) {
        _uiState.update { currentState ->
            val filteredAndSorted = applyFiltersAndSort(
                currentState.repositories,
                currentState.searchQuery,
                option,
                currentState.sortOption
            )
            currentState.copy(
                filterOption = option,
                filteredRepositories = filteredAndSorted
            )
        }
    }

    private fun resetFilters() {
        _uiState.update { currentState ->
            val filteredAndSorted = applyFiltersAndSort(
                currentState.repositories,
                "",
                FilterOption.ALL,
                SortOption.NAME_ASC
            )
            currentState.copy(
                searchQuery = "",
                filterOption = FilterOption.ALL,
                sortOption = SortOption.NAME_ASC,
                filteredRepositories = filteredAndSorted
            )
        }
    }

    private fun applyFiltersAndSort(
        repositories: List<com.example.sa.domain.model.Repository>,
        query: String,
        filterOption: FilterOption,
        sortOption: SortOption
    ): List<com.example.sa.domain.model.Repository> {
        val searched = searchRepositoriesUseCase(repositories, query)
        val filtered = filterRepositoriesUseCase(searched, filterOption)
        return sortRepositoriesUseCase(filtered, sortOption)
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
