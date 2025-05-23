package com.example.sa.ui.screens.repositorydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sa.domain.usecase.GetOrganizationRepositoryByNameUseCase
import com.example.sa.domain.usecase.GetRepositoryIssuesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryDetailViewModel @Inject constructor(
    private val getRepositoryUseCase: GetOrganizationRepositoryByNameUseCase,
    private val getIssuesUseCase: GetRepositoryIssuesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoryDetailState(isLoading = true))
    val uiState: StateFlow<RepositoryDetailState> = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow<RepositoryDetailIntent>()
    
    private val repositoryName: String = checkNotNull(savedStateHandle["repositoryName"])
    private val owner: String = "intuit" // Hardcoded for simplicity, could be passed via SavedStateHandle

    init {
        processIntents()
        processIntent(RepositoryDetailIntent.LoadRepository)
    }
    
    fun processIntent(intent: RepositoryDetailIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }
    
    private fun processIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                when (intent) {
                    is RepositoryDetailIntent.LoadRepository -> loadRepository()
                    is RepositoryDetailIntent.LoadIssues -> loadIssues(intent.state)
                }
            }
        }
    }

    private fun loadRepository() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getRepositoryUseCase(repositoryName).collect { result ->
                result.fold(
                    onSuccess = { repository ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                repository = repository
                            )
                        }
                        // Automatically load issues after repository is loaded
                        loadIssues("open")
                        loadIssues("closed")
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Unknown error"
                            )
                        }
                    }
                )
            }
        }
    }

    private fun loadIssues(state: String) {
        viewModelScope.launch {
            when (state) {
                "open" -> {
                    _uiState.update { it.copy(isLoadingOpenIssues = true, openIssuesError = null) }
                    
                    getIssuesUseCase(owner, repositoryName, state).collect { result ->
                        result.fold(
                            onSuccess = { issues ->
                                _uiState.update { 
                                    it.copy(
                                        isLoadingOpenIssues = false,
                                        openIssues = issues
                                    )
                                }
                            },
                            onFailure = { error ->
                                _uiState.update { 
                                    it.copy(
                                        isLoadingOpenIssues = false,
                                        openIssuesError = error.message ?: "Unknown error"
                                    )
                                }
                            }
                        )
                    }
                }
                "closed" -> {
                    _uiState.update { it.copy(isLoadingClosedIssues = true, closedIssuesError = null) }
                    
                    getIssuesUseCase(owner, repositoryName, state).collect { result ->
                        result.fold(
                            onSuccess = { issues ->
                                _uiState.update { 
                                    it.copy(
                                        isLoadingClosedIssues = false,
                                        closedIssues = issues
                                    )
                                }
                            },
                            onFailure = { error ->
                                _uiState.update { 
                                    it.copy(
                                        isLoadingClosedIssues = false,
                                        closedIssuesError = error.message ?: "Unknown error"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
} 