package com.example.sa.ui.screens.repositorydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sa.BuildConfig
import com.example.sa.domain.usecase.GetIssuesUseCase
import com.example.sa.domain.usecase.GetRepositoryUseCase
import com.example.sa.domain.usecase.RefreshRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryDetailViewModel @Inject constructor(
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getIssuesUseCase: GetIssuesUseCase,
    private val refreshRepositoryUseCase: RefreshRepositoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoryDetailState(isLoading = true))
    val uiState: StateFlow<RepositoryDetailState> = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow<RepositoryDetailIntent>()

    private var observeRepositoryJob: Job? = null
    private var refreshRepositoryJob: Job? = null
    private var openIssuesJob: Job? = null
    private var closedIssuesJob: Job? = null
    
    private val repositoryName: String = checkNotNull(savedStateHandle["repositoryName"])
    private val owner: String = BuildConfig.GITHUB_ORG

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
        startObservingRepository()
        refreshRepository()
    }

    private fun startObservingRepository() {
        observeRepositoryJob?.cancel()
        observeRepositoryJob = viewModelScope.launch {
            getRepositoryUseCase(owner, repositoryName).collectLatest { result ->
                result.fold(
                    onSuccess = { repository ->
                        val shouldTriggerIssues = _uiState.value.repository == null
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                repository = repository,
                                error = null
                            )
                        }
                        if (shouldTriggerIssues) {
                            loadIssues("open")
                            loadIssues("closed")
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update { currentState ->
                            val message = throwable.message ?: "Unknown error"
                            if (currentState.isRefreshing) {
                                currentState
                            } else {
                                currentState.copy(isLoading = false, error = message)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun refreshRepository() {
        refreshRepositoryJob?.cancel()
        refreshRepositoryJob = viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = currentState.repository == null,
                    isRefreshing = true,
                    error = null,
                    refreshError = null
                )
            }
            try {
                refreshRepositoryUseCase(owner, repositoryName)
            } catch (t: Throwable) {
                _uiState.update { currentState ->
                    val message = t.message ?: "Unknown error"
                    if (currentState.repository == null) {
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

    private fun loadIssues(state: String) {
        when (state) {
            "open" -> {
                openIssuesJob?.cancel()
                _uiState.update { it.copy(isLoadingOpenIssues = true, openIssuesError = null) }

                openIssuesJob = viewModelScope.launch {
                    getIssuesUseCase(owner, repositoryName, state).collectLatest { result ->
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
            }
            "closed" -> {
                closedIssuesJob?.cancel()
                _uiState.update { it.copy(isLoadingClosedIssues = true, closedIssuesError = null) }

                closedIssuesJob = viewModelScope.launch {
                    getIssuesUseCase(owner, repositoryName, state).collectLatest { result ->
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
