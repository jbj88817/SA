package com.example.sa.ui.screens.repositorydetail

import com.example.sa.domain.model.Issue
import com.example.sa.domain.model.Repository

/**
 * Represents the UI state for the Repository Detail screen
 */
data class RepositoryDetailState(
    val isLoading: Boolean = false,
    val repository: Repository? = null,
    val openIssues: List<Issue> = emptyList(),
    val closedIssues: List<Issue> = emptyList(),
    val isLoadingOpenIssues: Boolean = false,
    val isLoadingClosedIssues: Boolean = false,
    val openIssuesError: String? = null,
    val closedIssuesError: String? = null,
    val error: String? = null
) {
    val hasRepository: Boolean
        get() = repository != null
        
    val hasOpenIssues: Boolean
        get() = openIssues.isNotEmpty()
        
    val hasClosedIssues: Boolean
        get() = closedIssues.isNotEmpty()
} 