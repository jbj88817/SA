package com.example.sa.ui.screens.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.usecase.GetRepositoriesUseCase
import com.example.sa.domain.usecase.RefreshRepositoriesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RepositoriesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getRepositoriesUseCase: GetRepositoriesUseCase
    private lateinit var refreshRepositoriesUseCase: RefreshRepositoriesUseCase
    private lateinit var viewModel: RepositoriesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRepositoriesUseCase = mock()
        refreshRepositoriesUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls loadRepositories`() = runTest {
        // Given
        val repositories = listOf(
            Repository(
                id = 1,
                name = "repo1",
                description = "description1",
                language = "Kotlin",
                stargazersCount = 100,
                forksCount = 50,
                openIssuesCount = 10,
                htmlUrl = "https://github.com/intuit/repo1",
                createdAt = "2021-01-01",
                updatedAt = "2021-01-02"
            )
        )
        
        whenever(getRepositoriesUseCase("intuit"))
            .thenReturn(flowOf(Result.Success(repositories)))

        // When
        viewModel = RepositoriesViewModel(getRepositoriesUseCase, refreshRepositoriesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(refreshRepositoriesUseCase).invoke("intuit")
        assertTrue(viewModel.uiState.value is RepositoriesUiState.Success)
        assertEquals(
            repositories,
            (viewModel.uiState.value as RepositoriesUiState.Success).repositories
        )
    }

    @Test
    fun `loadRepositories sets Loading state`() = runTest {
        // Given
        whenever(getRepositoriesUseCase("intuit"))
            .thenReturn(flowOf(Result.Loading))

        // When
        viewModel = RepositoriesViewModel(getRepositoriesUseCase, refreshRepositoriesUseCase)

        // Then
        assertTrue(viewModel.uiState.value is RepositoriesUiState.Loading)
    }

    @Test
    fun `loadRepositories sets Error state when repository returns error`() = runTest {
        // Given
        val errorMessage = "Error loading repositories"
        whenever(getRepositoriesUseCase("intuit"))
            .thenReturn(flowOf(Result.Error(Exception(errorMessage))))
        
        // When
        viewModel = RepositoriesViewModel(getRepositoriesUseCase, refreshRepositoriesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is RepositoriesUiState.Error)
        assertEquals(
            errorMessage,
            (viewModel.uiState.value as RepositoriesUiState.Error).message
        )
    }

    @Test
    fun `loadRepositories sets Empty state when repository returns empty list`() = runTest {
        // Given
        whenever(getRepositoriesUseCase("intuit"))
            .thenReturn(flowOf(Result.Success(emptyList())))
        
        // When
        viewModel = RepositoriesViewModel(getRepositoriesUseCase, refreshRepositoriesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is RepositoriesUiState.Empty)
    }
} 