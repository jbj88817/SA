package com.example.sa.ui.screens.repositorydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sa.domain.model.Issue
import com.example.sa.domain.model.Repository
import com.example.sa.domain.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryDetailViewModel @Inject constructor(
    private val repository: GithubRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _repositoryState = MutableStateFlow<RepositoryDetailUiState>(RepositoryDetailUiState.Loading)
    val repositoryState: StateFlow<RepositoryDetailUiState> = _repositoryState

    private val _openIssuesState = MutableStateFlow<IssuesUiState>(IssuesUiState.Loading)
    val openIssuesState: StateFlow<IssuesUiState> = _openIssuesState

    private val _closedIssuesState = MutableStateFlow<IssuesUiState>(IssuesUiState.Loading)
    val closedIssuesState: StateFlow<IssuesUiState> = _closedIssuesState

    private val repositoryName: String = checkNotNull(savedStateHandle["repositoryName"])

    init {
        loadRepository()
        // For now, we'll just show empty states for issues since we're focusing on repositories
        _openIssuesState.value = IssuesUiState.Empty
        _closedIssuesState.value = IssuesUiState.Empty
    }

    fun loadRepository() {
        viewModelScope.launch {
            _repositoryState.value = RepositoryDetailUiState.Loading
            try {
                val repo = repository.getRepository(repositoryName)
                _repositoryState.value = RepositoryDetailUiState.Success(repo)
            } catch (e: Exception) {
                _repositoryState.value = RepositoryDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadIssues(state: String) {
        // For now, we'll just show empty states for issues since we're focusing on repositories
        val stateFlow = if (state == "open") _openIssuesState else _closedIssuesState
        stateFlow.value = IssuesUiState.Empty
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