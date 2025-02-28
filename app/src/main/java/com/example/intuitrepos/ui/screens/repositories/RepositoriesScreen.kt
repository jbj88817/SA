package com.example.intuitrepos.ui.screens.repositories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.intuitrepos.R
import com.example.intuitrepos.domain.model.Repository
import com.example.intuitrepos.ui.components.ErrorComponent
import com.example.intuitrepos.ui.components.LoadingComponent
import com.example.intuitrepos.ui.components.RepositoryItem

@Composable
fun RepositoriesScreen(
    viewModel: RepositoriesViewModel = hiltViewModel(),
    onRepositoryClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.repositories)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is RepositoriesUiState.Loading -> {
                    LoadingComponent()
                }
                is RepositoriesUiState.Success -> {
                    RepositoriesList(
                        repositories = state.repositories,
                        onRepositoryClick = { repository ->
                            onRepositoryClick(repository.name)
                        }
                    )
                }
                is RepositoriesUiState.Error -> {
                    ErrorComponent(
                        message = stringResource(R.string.error_loading_repos),
                        onRetry = { viewModel.loadRepositories() }
                    )
                }
                is RepositoriesUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_repositories_found),
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RepositoriesList(
    repositories: List<Repository>,
    onRepositoryClick: (Repository) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(repositories) { repository ->
            RepositoryItem(
                repository = repository,
                onClick = onRepositoryClick
            )
        }
    }
} 