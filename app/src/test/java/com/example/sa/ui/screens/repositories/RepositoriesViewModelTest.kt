package com.example.sa.ui.screens.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.usecase.FilterRepositoriesUseCase
import com.example.sa.domain.usecase.GetOrganizationRepositoriesUseCase
import com.example.sa.domain.usecase.SearchRepositoriesUseCase
import com.example.sa.domain.usecase.SortRepositoriesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RepositoriesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: RepositoriesViewModel
    private lateinit var getRepositoriesUseCase: GetOrganizationRepositoriesUseCase
    private lateinit var searchRepositoriesUseCase: SearchRepositoriesUseCase
    private lateinit var filterRepositoriesUseCase: FilterRepositoriesUseCase
    private lateinit var sortRepositoriesUseCase: SortRepositoriesUseCase
    
    private lateinit var repositories: List<Repository>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        getRepositoriesUseCase = mock()
        searchRepositoriesUseCase = mock()
        filterRepositoriesUseCase = mock()
        sortRepositoriesUseCase = mock()
        
        repositories = listOf(
            Repository(
                id = 1,
                name = "android-app",
                description = "Android application",
                language = "Kotlin",
                stars = 100,
                forks = 50,
                openIssuesCount = 10,
                htmlUrl = "https://github.com/test/android-app",
                createdAt = "2021-01-01",
                updatedAt = "2021-02-01",
                topics = listOf("android", "kotlin")
            ),
            Repository(
                id = 2,
                name = "ios-app",
                description = "iOS application",
                language = "Swift",
                stars = 200,
                forks = 100,
                openIssuesCount = 20,
                htmlUrl = "https://github.com/test/ios-app",
                createdAt = "2021-01-02",
                updatedAt = "2021-02-02",
                topics = listOf("ios", "swift")
            )
        )
        
        // Default behavior for use cases
        whenever(searchRepositoriesUseCase(repositories, "")).thenReturn(repositories)
        whenever(filterRepositoriesUseCase(repositories, FilterOption.ALL)).thenReturn(repositories)
        whenever(sortRepositoriesUseCase(repositories, SortOption.NAME_ASC)).thenReturn(repositories)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        whenever(getRepositoriesUseCase()).thenReturn(flowOf(Result.Success(repositories)))
        
        // When
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        
        // Then
        assertTrue(viewModel.uiState.value.isLoading)
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertEquals(FilterOption.ALL, viewModel.uiState.value.filterOption)
        assertEquals(SortOption.NAME_ASC, viewModel.uiState.value.sortOption)
        assertEquals(emptyList<Repository>(), viewModel.uiState.value.repositories)
        assertEquals(emptyList<Repository>(), viewModel.uiState.value.filteredRepositories)
        assertNull(viewModel.uiState.value.error)
        
        // Advance time to allow the flow collection
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then state should be updated with repositories
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(repositories, viewModel.uiState.value.repositories)
        assertEquals(repositories, viewModel.uiState.value.filteredRepositories)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `when LoadRepositories intent is processed, repositories are loaded`() = runTest {
        // Given
        whenever(getRepositoriesUseCase()).thenReturn(flowOf(Result.Success(repositories)))
        
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Reset to loading state
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        
        // When
        viewModel.processIntent(RepositoriesIntent.LoadRepositories)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(repositories, viewModel.uiState.value.repositories)
        assertEquals(repositories, viewModel.uiState.value.filteredRepositories)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `when UpdateSearchQuery intent is processed, search is applied`() = runTest {
        // Given
        whenever(getRepositoriesUseCase()).thenReturn(flowOf(Result.Success(repositories)))
        
        val searchQuery = "android"
        val searchResults = listOf(repositories[0])
        whenever(searchRepositoriesUseCase(repositories, searchQuery)).thenReturn(searchResults)
        whenever(filterRepositoriesUseCase(searchResults, FilterOption.ALL)).thenReturn(searchResults)
        whenever(sortRepositoriesUseCase(searchResults, SortOption.NAME_ASC)).thenReturn(searchResults)
        
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoriesIntent.UpdateSearchQuery(searchQuery))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(searchQuery, viewModel.uiState.value.searchQuery)
        assertEquals(searchResults, viewModel.uiState.value.filteredRepositories)
    }
    
    @Test
    fun `when ClearSearch intent is processed, search is cleared`() = runTest {
        // Given
        whenever(getRepositoriesUseCase()).thenReturn(flowOf(Result.Success(repositories)))
        
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Set a search query first
        val searchQuery = "android"
        val searchResults = listOf(repositories[0])
        whenever(searchRepositoriesUseCase(repositories, searchQuery)).thenReturn(searchResults)
        whenever(filterRepositoriesUseCase(searchResults, FilterOption.ALL)).thenReturn(searchResults)
        whenever(sortRepositoriesUseCase(searchResults, SortOption.NAME_ASC)).thenReturn(searchResults)
        
        viewModel.processIntent(RepositoriesIntent.UpdateSearchQuery(searchQuery))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoriesIntent.ClearSearch)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertEquals(repositories, viewModel.uiState.value.filteredRepositories)
    }
    
    @Test
    fun `when UpdateFilterOption intent is processed, filter is applied`() = runTest {
        // Given
        whenever(getRepositoriesUseCase()).thenReturn(flowOf(Result.Success(repositories)))
        
        val filterOption = FilterOption.KOTLIN
        val filterResults = listOf(repositories[0])
        whenever(filterRepositoriesUseCase(repositories, filterOption)).thenReturn(filterResults)
        whenever(sortRepositoriesUseCase(filterResults, SortOption.NAME_ASC)).thenReturn(filterResults)
        
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoriesIntent.UpdateFilterOption(filterOption))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(filterOption, viewModel.uiState.value.filterOption)
        assertEquals(filterResults, viewModel.uiState.value.filteredRepositories)
    }
    
    @Test
    fun `when UpdateSortOption intent is processed, sort is applied`() = runTest {
        // Given
        whenever(getRepositoriesUseCase()).thenReturn(flowOf(Result.Success(repositories)))
        
        val sortOption = SortOption.STARS_DESC
        val sortedResults = listOf(repositories[1], repositories[0])
        whenever(sortRepositoriesUseCase(repositories, sortOption)).thenReturn(sortedResults)
        
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoriesIntent.UpdateSortOption(sortOption))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(sortOption, viewModel.uiState.value.sortOption)
        assertEquals(sortedResults, viewModel.uiState.value.filteredRepositories)
    }
    
    @Test
        whenever(sortRepositoriesUseCase(repositories, sortOption)).thenReturn(sortedResults)
        
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Set search, filter, and sort options
        val searchQuery = "android"
        val filterOption = FilterOption.KOTLIN
        val sortOption = SortOption.STARS_DESC
        
        val searchResults = listOf(repositories[0])
        whenever(searchRepositoriesUseCase(repositories, searchQuery)).thenReturn(searchResults)
        
        val filterResults = listOf(repositories[0])
        whenever(filterRepositoriesUseCase(searchResults, filterOption)).thenReturn(filterResults)
        
        val sortedResults = listOf(repositories[0])
        whenever(sortRepositoriesUseCase(filterResults, sortOption)).thenReturn(sortedResults)
        
        viewModel.processIntent(RepositoriesIntent.UpdateSearchQuery(searchQuery))
        viewModel.processIntent(RepositoriesIntent.UpdateFilterOption(filterOption))
        viewModel.processIntent(RepositoriesIntent.UpdateSortOption(sortOption))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoriesIntent.ResetFilters)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertEquals(FilterOption.ALL, viewModel.uiState.value.filterOption)
        assertEquals(SortOption.NAME_ASC, viewModel.uiState.value.sortOption)
        assertEquals(repositories, viewModel.uiState.value.filteredRepositories)
    }
    
    @Test
    fun `when getOrganizationRepositoriesUseCase returns error, error state is set`() = runTest {
        // Given
        val errorMessage = "Error loading repositories"
        whenever(getRepositoriesUseCase()).thenReturn(flowOf(Result.Error(RuntimeException(errorMessage))))
        
        // When
        viewModel = RepositoriesViewModel(
            getRepositoriesUseCase,
            searchRepositoriesUseCase,
            filterRepositoriesUseCase,
            sortRepositoriesUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertEquals(emptyList<Repository>(), viewModel.uiState.value.repositories)
    }
} 