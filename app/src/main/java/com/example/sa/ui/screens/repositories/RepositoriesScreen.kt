package com.example.sa.ui.screens.repositories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    val keyboardController = LocalSoftwareKeyboardController.current
    
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    
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
                actions = {
                    // Reset button
                    if (uiState.searchQuery.isNotEmpty() || 
                        uiState.filterOption != FilterOption.ALL || 
                        uiState.sortOption != SortOption.NAME_ASC) {
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                    uiState.isLoading -> {
                        LoadingComponent()
                    }
                    uiState.error != null -> {
                        ErrorComponent(
                            message = uiState.error ?: stringResource(R.string.error_loading_repos),
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
                                style = MaterialTheme.typography.h6,
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
    currentFilter: FilterOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "🔍 " + stringResource(R.string.filter) + ": " + 
                stringResource(currentFilter.stringResId),
            maxLines = 1
        )
    }
}

@Composable
fun SortButton(
    currentSort: SortOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "↕️ " + stringResource(R.string.sort) + ": " + 
                stringResource(currentSort.stringResId),
            maxLines = 1
        )
    }
}

@Composable
fun FilterDialog(
    currentFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter_by),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                FilterOption.values().forEach { option ->
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
                            text = stringResource(option.stringResId),
                            style = MaterialTheme.typography.body1,
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
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.sort_by),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                SortOption.values().forEach { option ->
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
                            text = stringResource(option.stringResId),
                            style = MaterialTheme.typography.body1,
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