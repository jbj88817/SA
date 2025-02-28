package com.example.sa.ui.screens.repositorydetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sa.R
import com.example.sa.domain.model.Issue
import com.example.sa.domain.model.Repository
import com.example.sa.ui.components.ErrorComponent
import com.example.sa.ui.components.IssueItem
import com.example.sa.ui.components.LoadingComponent

@Composable
fun RepositoryDetailScreen(
    viewModel: RepositoryDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.repository_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingComponent()
                }
                uiState.error != null -> {
                    ErrorComponent(
                        message = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.processIntent(RepositoryDetailIntent.LoadRepository) }
                    )
                }
                uiState.hasRepository -> {
                    RepositoryDetailContent(
                        repository = uiState.repository!!,
                        uiState = uiState,
                        onRetryOpenIssues = { viewModel.processIntent(RepositoryDetailIntent.LoadIssues("open")) },
                        onRetryClosedIssues = { viewModel.processIntent(RepositoryDetailIntent.LoadIssues("closed")) }
                    )
                }
            }
        }
    }
}

@Composable
fun RepositoryDetailContent(
    repository: Repository,
    uiState: RepositoryDetailState,
    onRetryOpenIssues: () -> Unit,
    onRetryClosedIssues: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.open_issues), stringResource(R.string.closed_issues))
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Repository info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = repository.name,
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.primary
                )
                
                if (repository.description != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = repository.description,
                        style = MaterialTheme.typography.body1
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.stars, repository.stars),
                        style = MaterialTheme.typography.body2
                    )
                    
                    Text(
                        text = stringResource(R.string.forks, repository.forks),
                        style = MaterialTheme.typography.body2
                    )
                    
                    Text(
                        text = if (repository.language != null) {
                            stringResource(R.string.language, repository.language)
                        } else {
                            stringResource(R.string.no_language)
                        },
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
        
        // Tab layout for issues
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
        
        // Issues content based on selected tab
        when (selectedTabIndex) {
            0 -> {
                // Open issues
                IssuesContent(
                    isLoading = uiState.isLoadingOpenIssues,
                    issues = uiState.openIssues,
                    error = uiState.openIssuesError,
                    onRetry = onRetryOpenIssues
                )
            }
            1 -> {
                // Closed issues
                IssuesContent(
                    isLoading = uiState.isLoadingClosedIssues,
                    issues = uiState.closedIssues,
                    error = uiState.closedIssuesError,
                    onRetry = onRetryClosedIssues
                )
            }
        }
    }
}

@Composable
fun IssuesContent(
    isLoading: Boolean,
    issues: List<Issue>,
    error: String?,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                LoadingComponent()
            }
            error != null -> {
                ErrorComponent(
                    message = error,
                    onRetry = onRetry
                )
            }
            issues.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_issues_found),
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                IssuesList(issues = issues)
            }
        }
    }
}

@Composable
fun IssuesList(issues: List<Issue>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(issues) { issue ->
            IssueItem(issue = issue)
        }
    }
} 