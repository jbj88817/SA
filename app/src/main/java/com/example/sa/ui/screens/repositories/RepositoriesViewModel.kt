package com.example.sa.ui.screens.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.usecase.GetRepositoriesUseCase
import com.example.sa.domain.usecase.RefreshRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    private val refreshRepositoriesUseCase: RefreshRepositoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepositoriesUiState>(RepositoriesUiState.Loading)
    val uiState: StateFlow<RepositoriesUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val organization = "intuit"
    private var allRepositories = listOf<Repository>()

    init {
        loadRepositories()
    }

    fun loadRepositories() {
        viewModelScope.launch {
            _uiState.value = RepositoriesUiState.Loading
            try {
                refreshRepositoriesUseCase(organization)
            } catch (e: Exception) {
                // If refresh fails, we'll still try to load from local database
            }
            
            getRepositoriesUseCase(organization).collect { result ->
                when (result) {
                    is Result.Success -> {
                        allRepositories = result.data
                        applySearchFilter()
                    }
                    is Result.Error -> _uiState.value = RepositoriesUiState.Error(result.exception.message ?: "Unknown error")
                    is Result.Loading -> _uiState.value = RepositoriesUiState.Loading
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applySearchFilter()
    }

    private fun applySearchFilter() {
        val query = _searchQuery.value.trim().lowercase()
        if (allRepositories.isEmpty()) {
            _uiState.value = RepositoriesUiState.Empty
            return
        }

        if (query.isEmpty()) {
            _uiState.value = RepositoriesUiState.Success(allRepositories)
            return
        }

        val filteredRepositories = allRepositories.filter { repository ->
            repository.name.lowercase().contains(query) || 
            (repository.description?.lowercase()?.contains(query) ?: false)
        }

        _uiState.value = if (filteredRepositories.isEmpty()) {
            RepositoriesUiState.Empty
        } else {
            RepositoriesUiState.Success(filteredRepositories)
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        applySearchFilter()
    }
}

sealed class RepositoriesUiState {
    object Loading : RepositoriesUiState()
    object Empty : RepositoriesUiState()
    data class Success(val repositories: List<Repository>) : RepositoriesUiState()
    data class Error(val message: String) : RepositoriesUiState()
} 