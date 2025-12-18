package com.example.sa.ui.screens.repositorydetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.sa.R
import com.example.sa.domain.model.Issue
import com.example.sa.domain.model.Repository
import com.example.sa.ui.components.ErrorComponent
import com.example.sa.ui.components.IssueItem
import com.example.sa.ui.components.LoadingComponent

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RepositoryDetailScreen(
    viewModel: RepositoryDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.refreshError) {
        uiState.refreshError?.let { snackbarHostState.showSnackbar(it) }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.repository_details)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isRefreshing && uiState.repository != null) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        LoadingComponent()
                    }
                    uiState.error != null && uiState.repository == null -> {
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
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = repository.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (repository.description != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = repository.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.stars, repository.stars),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = stringResource(R.string.forks, repository.forks),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = if (repository.language != null) {
                            stringResource(R.string.language, repository.language)
                        } else {
                            stringResource(R.string.no_language)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Tab layout for issues
        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
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
                        style = MaterialTheme.typography.titleMedium,
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
