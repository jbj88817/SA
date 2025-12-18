package com.example.sa.feature.repositories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.RepositoryFilter
import com.example.sa.domain.model.RepositorySort
import com.example.sa.core.ui.components.ErrorComponent
import com.example.sa.core.ui.components.LoadingComponent
import com.example.sa.core.R as CoreR

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RepositoriesScreen(
    viewModel: RepositoriesViewModel = hiltViewModel(),
    onRepositoryClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.refreshError) {
        uiState.refreshError?.let { snackbarHostState.showSnackbar(it) }
    }

    if (showFilterDialog) {
        FilterDialog(
            currentFilter = uiState.filterOption,
            onFilterSelected = { 
                viewModel.processIntent(RepositoriesIntent.UpdateFilterOption(it))
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
    
    if (showSortDialog) {
        SortDialog(
            currentSort = uiState.sortOption,
            onSortSelected = { 
                viewModel.processIntent(RepositoriesIntent.UpdateSortOption(it))
                showSortDialog = false
            },
            onDismiss = { showSortDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.repositories)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    // Reset button
                    if (uiState.searchQuery.isNotEmpty() || 
                        uiState.filterOption != RepositoryFilter.ALL ||
                        uiState.sortOption != RepositorySort.NAME_ASC) {
                        IconButton(onClick = { 
                            viewModel.processIntent(RepositoriesIntent.ResetFilters) 
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.reset)
                            )
                        }
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
            if (uiState.isRefreshing && uiState.repositories.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.processIntent(RepositoriesIntent.UpdateSearchQuery(it)) },
                onClearQuery = { viewModel.processIntent(RepositoriesIntent.ClearSearch) },
                onSearch = { keyboardController?.hide() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Filter and Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Filter Button
                FilterButton(
                    currentFilter = uiState.filterOption,
                    onClick = { showFilterDialog = true },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Sort Button
                SortButton(
                    currentSort = uiState.sortOption,
                    onClick = { showSortDialog = true },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when {
                    uiState.isLoading || (uiState.isRefreshing && uiState.repositories.isEmpty()) -> {
                        LoadingComponent()
                    }
                    uiState.error != null && uiState.repositories.isEmpty() -> {
                        ErrorComponent(
                            message = uiState.error ?: stringResource(R.string.error_loading_repos),
                            retryText = stringResource(CoreR.string.retry),
                            onRetry = { viewModel.processIntent(RepositoriesIntent.LoadRepositories) }
                        )
                    }
                    uiState.isEmpty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.searchQuery.isNotEmpty()) {
                                    stringResource(R.string.no_repositories_found_for_query, uiState.searchQuery)
                                } else {
                                    stringResource(R.string.no_repositories_found)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    else -> {
                        RepositoriesList(
                            repositories = uiState.filteredRepositories,
                            onRepositoryClick = { repository ->
                                onRepositoryClick(repository.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(
    currentFilter: RepositoryFilter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "🔍 " + stringResource(R.string.filter) + ": " + 
                stringResource(currentFilter.labelResId()),
            maxLines = 1
        )
    }
}

@Composable
fun SortButton(
    currentSort: RepositorySort,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "↕️ " + stringResource(R.string.sort) + ": " + 
                stringResource(currentSort.labelResId()),
            maxLines = 1
        )
    }
}

@Composable
fun FilterDialog(
    currentFilter: RepositoryFilter,
    onFilterSelected: (RepositoryFilter) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter_by),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                RepositoryFilter.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == currentFilter),
                                onClick = { onFilterSelected(option) }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == currentFilter),
                            onClick = { onFilterSelected(option) }
                        )
                        Text(
                            text = stringResource(option.labelResId()),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

@Composable
fun SortDialog(
    currentSort: RepositorySort,
    onSortSelected: (RepositorySort) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.sort_by),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                RepositorySort.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == currentSort),
                                onClick = { onSortSelected(option) }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == currentSort),
                            onClick = { onSortSelected(option) }
                        )
                        Text(
                            text = stringResource(option.labelResId()),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
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
