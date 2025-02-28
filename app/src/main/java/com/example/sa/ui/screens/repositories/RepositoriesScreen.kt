package com.example.sa.ui.screens.repositories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sa.R
import com.example.sa.domain.model.Repository
import com.example.sa.ui.components.ErrorComponent
import com.example.sa.ui.components.LoadingComponent
import com.example.sa.ui.components.RepositoryItem

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RepositoriesScreen(
    viewModel: RepositoriesViewModel = hiltViewModel(),
    onRepositoryClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.repositories)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onClearQuery = { viewModel.clearSearch() },
                onSearch = { keyboardController?.hide() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                                text = if (searchQuery.isNotEmpty()) {
                                    stringResource(R.string.no_repositories_found_for_query, searchQuery)
                                } else {
                                    stringResource(R.string.no_repositories_found)
                                },
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
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_repositories)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_icon)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_search))
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                keyboardController?.hide()
            }
        ),
        modifier = modifier
    )
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