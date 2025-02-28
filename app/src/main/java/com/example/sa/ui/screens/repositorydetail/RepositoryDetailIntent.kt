package com.example.sa.ui.screens.repositorydetail

/**
 * Represents user intents/actions for the Repository Detail screen
 */
sealed class RepositoryDetailIntent {
    object LoadRepository : RepositoryDetailIntent()
    data class LoadIssues(val state: String) : RepositoryDetailIntent()
} 