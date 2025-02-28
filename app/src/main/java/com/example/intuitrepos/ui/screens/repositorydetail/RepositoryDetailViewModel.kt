package com.example.intuitrepos.ui.screens.repositorydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intuitrepos.domain.model.Issue
import com.example.intuitrepos.domain.model.Repository
import com.example.intuitrepos.domain.model.Result
import com.example.intuitrepos.domain.usecase.GetIssuesUseCase
import com.example.intuitrepos.domain.usecase.GetRepositoryUseCase
import com.example.intuitrepos.domain.usecase.RefreshIssuesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryDetailViewModel @Inject constructor(
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getIssuesUseCase: GetIssuesUseCase,
    private val refreshIssuesUseCase: RefreshIssuesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _repositoryState = MutableStateFlow<RepositoryDetailUiState>(RepositoryDetailUiState.Loading)
    val repositoryState: StateFlow<RepositoryDetailUiState> = _repositoryState

    private val _openIssuesState = MutableStateFlow<IssuesUiState>(IssuesUiState.Loading)
    val openIssuesState: StateFlow<IssuesUiState> = _openIssuesState

    private val _closedIssuesState = MutableStateFlow<IssuesUiState>(IssuesUiState.Loading)
    val closedIssuesState: StateFlow<IssuesUiState> = _closedIssuesState

    private val organization = "intuit"
    private val repositoryName: String = checkNotNull(savedStateHandle["repositoryName"])

    init {
        loadRepository()
        loadIssues("open")
        loadIssues("closed")
    }

    fun loadRepository() {
        viewModelScope.launch {
            getRepositoryUseCase(organization, repositoryName).collect { result ->
                _repositoryState.value = when (result) {
                    is Result.Success -> RepositoryDetailUiState.Success(result.data)
                    is Result.Error -> RepositoryDetailUiState.Error(result.exception.message ?: "Unknown error")
                    is Result.Loading -> RepositoryDetailUiState.Loading
                }
            }
        }
    }

    fun loadIssues(state: String) {
        viewModelScope.launch {
            val stateFlow = if (state == "open") _openIssuesState else _closedIssuesState
            stateFlow.value = IssuesUiState.Loading
            
            try {
                refreshIssuesUseCase(organization, repositoryName, state)
            } catch (e: Exception) {
                // If refresh fails, we'll still try to load from local database
            }
            
            getIssuesUseCase(organization, repositoryName, state).collect { result ->
                stateFlow.value = when (result) {
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            IssuesUiState.Empty
                        } else {
                            IssuesUiState.Success(result.data)
                        }
                    }
                    is Result.Error -> IssuesUiState.Error(result.exception.message ?: "Unknown error")
                    is Result.Loading -> IssuesUiState.Loading
                }
            }
        }
    }
}

sealed class RepositoryDetailUiState {
    object Loading : RepositoryDetailUiState()
    data class Success(val repository: Repository) : RepositoryDetailUiState()
    data class Error(val message: String) : RepositoryDetailUiState()
}

sealed class IssuesUiState {
    object Loading : IssuesUiState()
    object Empty : IssuesUiState()
    data class Success(val issues: List<Issue>) : IssuesUiState()
    data class Error(val message: String) : IssuesUiState()
} 