package com.example.intuitrepos.ui.screens.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intuitrepos.domain.model.Repository
import com.example.intuitrepos.domain.model.Result
import com.example.intuitrepos.domain.usecase.GetRepositoriesUseCase
import com.example.intuitrepos.domain.usecase.RefreshRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    private val refreshRepositoriesUseCase: RefreshRepositoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepositoriesUiState>(RepositoriesUiState.Loading)
    val uiState: StateFlow<RepositoriesUiState> = _uiState

    private val organization = "intuit"

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
                _uiState.value = when (result) {
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            RepositoriesUiState.Empty
                        } else {
                            RepositoriesUiState.Success(result.data)
                        }
                    }
                    is Result.Error -> RepositoriesUiState.Error(result.exception.message ?: "Unknown error")
                    is Result.Loading -> RepositoriesUiState.Loading
                }
            }
        }
    }
}

sealed class RepositoriesUiState {
    object Loading : RepositoriesUiState()
    object Empty : RepositoriesUiState()
    data class Success(val repositories: List<Repository>) : RepositoriesUiState()
    data class Error(val message: String) : RepositoriesUiState()
} 