package com.example.sa.ui.screens.repositorydetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.sa.domain.model.Issue
import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.model.User
import com.example.sa.domain.usecase.GetOrganizationRepositoryByNameUseCase
import com.example.sa.domain.usecase.GetRepositoryIssuesUseCase
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
class RepositoryDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: RepositoryDetailViewModel
    private lateinit var getRepositoryUseCase: GetOrganizationRepositoryByNameUseCase
    private lateinit var getIssuesUseCase: GetRepositoryIssuesUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    
    private lateinit var repository: Repository
    private lateinit var openIssues: List<Issue>
    private lateinit var closedIssues: List<Issue>
    
    private val repositoryName = "test-repo"
    private val owner = "intuit"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        getRepositoryUseCase = mock()
        getIssuesUseCase = mock()
        
        savedStateHandle = SavedStateHandle().apply {
            set("repositoryName", repositoryName)
        }
        
        repository = Repository(
            id = 1,
            name = repositoryName,
            description = "Test repository",
            language = "Kotlin",
            stars = 100,
            forks = 50,
            openIssuesCount = 10,
            htmlUrl = "https://github.com/test/test-repo",
            createdAt = "2021-01-01",
            updatedAt = "2021-02-01",
            topics = listOf("android", "kotlin")
        )
        
        val user = User(
            id = 1,
            login = "testuser",
            avatarUrl = "https://github.com/testuser.png"
        )
        
        openIssues = listOf(
            Issue(
                id = 1,
                number = 1,
                title = "Open issue 1",
                state = "open",
                body = "This is an open issue",
                createdAt = "2021-01-01",
                updatedAt = "2021-01-02",
                htmlUrl = "https://github.com/test/test-repo/issues/1",
                user = user
            ),
            Issue(
                id = 2,
                number = 2,
                title = "Open issue 2",
                state = "open",
                body = "This is another open issue",
                createdAt = "2021-01-03",
                updatedAt = "2021-01-04",
                htmlUrl = "https://github.com/test/test-repo/issues/2",
                user = user
            )
        )
        
        closedIssues = listOf(
            Issue(
                id = 3,
                number = 3,
                title = "Closed issue 1",
                state = "closed",
                body = "This is a closed issue",
                createdAt = "2021-01-05",
                updatedAt = "2021-01-06",
                htmlUrl = "https://github.com/test/test-repo/issues/3",
                user = user
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        whenever(getRepositoryUseCase(repositoryName)).thenReturn(flowOf(Result.Success(repository)))
        
        // When
        viewModel = RepositoryDetailViewModel(
            getRepositoryUseCase,
            getIssuesUseCase,
            savedStateHandle
        )
        
        // Then
        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.repository)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoadingOpenIssues)
        assertFalse(viewModel.uiState.value.isLoadingClosedIssues)
        assertEquals(emptyList<Issue>(), viewModel.uiState.value.openIssues)
        assertEquals(emptyList<Issue>(), viewModel.uiState.value.closedIssues)
        
        // Advance time to allow the flow collection
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then state should be updated with repository
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(repository, viewModel.uiState.value.repository)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `when LoadRepository intent is processed, repository is loaded`() = runTest {
        // Given
        whenever(getRepositoryUseCase(repositoryName)).thenReturn(flowOf(Result.Success(repository)))
        
        viewModel = RepositoryDetailViewModel(
            getRepositoryUseCase,
            getIssuesUseCase,
            savedStateHandle
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Reset to loading state
        viewModel = RepositoryDetailViewModel(
            getRepositoryUseCase,
            getIssuesUseCase,
            savedStateHandle
        )
        
        // When
        viewModel.processIntent(RepositoryDetailIntent.LoadRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(repository, viewModel.uiState.value.repository)
        assertNull(viewModel.uiState.value.error)
    }
    
    @Test
    fun `when LoadIssues intent with open state is processed, open issues are loaded`() = runTest {
        // Given
        whenever(getRepositoryUseCase(repositoryName)).thenReturn(flowOf(Result.Success(repository)))
        whenever(getIssuesUseCase(owner, repositoryName, "open")).thenReturn(flowOf(Result.Success(openIssues)))
        
        viewModel = RepositoryDetailViewModel(
            getRepositoryUseCase,
            getIssuesUseCase,
            savedStateHandle
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoryDetailIntent.LoadIssues("open"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoadingOpenIssues)
        assertEquals(openIssues, viewModel.uiState.value.openIssues)
        assertNull(viewModel.uiState.value.openIssuesError)
    }
    
    @Test
    fun `when LoadIssues intent with closed state is processed, closed issues are loaded`() = runTest {
        // Given
        whenever(getRepositoryUseCase(repositoryName)).thenReturn(flowOf(Result.Success(repository)))
        whenever(getIssuesUseCase(owner, repositoryName, "closed")).thenReturn(flowOf(Result.Success(closedIssues)))
        
        viewModel = RepositoryDetailViewModel(
            getRepositoryUseCase,
            getIssuesUseCase,
            savedStateHandle
        )
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoryDetailIntent.LoadIssues("closed"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoadingClosedIssues)
        assertEquals(closedIssues, viewModel.uiState.value.closedIssues)
        assertNull(viewModel.uiState.value.closedIssuesError)
    }
    
    @Test
    fun `when getRepositoryUseCase returns error, error state is set`() = runTest {
        // Given
        val errorMessage = "Error loading repository"
        whenever(getRepositoryUseCase(repositoryName)).thenReturn(flowOf(Result.Error(RuntimeException(errorMessage))))
        
        // When
        viewModel = RepositoryDetailViewModel(
            getRepositoryUseCase,
            getIssuesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.repository)
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }
    
    @Test
    fun `when getIssuesUseCase for open issues returns error, open issues error state is set`() = runTest {
        // Given
        val errorMessage = "Error loading open issues"
        whenever(getRepositoryUseCase(repositoryName)).thenReturn(flowOf(Result.Success(repository)))
        whenever(getIssuesUseCase(owner, repositoryName, "open")).thenReturn(flowOf(Result.Error(RuntimeException(errorMessage))))
        
        viewModel = RepositoryDetailViewModel(
            getRepositoryUseCase,
            getIssuesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoryDetailIntent.LoadIssues("open"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoadingOpenIssues)
        assertEquals(emptyList<Issue>(), viewModel.uiState.value.openIssues)
        assertEquals(errorMessage, viewModel.uiState.value.openIssuesError)
    }
    
    @Test
    fun `when getIssuesUseCase for closed issues returns error, closed issues error state is set`() = runTest {
        // Given
        val errorMessage = "Error loading closed issues"
        whenever(getRepositoryUseCase(repositoryName)).thenReturn(flowOf(Result.Success(repository)))
        whenever(getIssuesUseCase(owner, repositoryName, "closed")).thenReturn(flowOf(Result.Error(RuntimeException(errorMessage))))
        
        viewModel = RepositoryDetailViewModel(
            getRepositoryUseCase,
            getIssuesUseCase,
            savedStateHandle
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.processIntent(RepositoryDetailIntent.LoadIssues("closed"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isLoadingClosedIssues)
        assertEquals(emptyList<Issue>(), viewModel.uiState.value.closedIssues)
        assertEquals(errorMessage, viewModel.uiState.value.closedIssuesError)
    }
} 