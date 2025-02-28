package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.GithubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetRepositoriesUseCaseTest {
    
    private lateinit var getRepositoriesUseCase: GetRepositoriesUseCase
    private lateinit var mockRepository: GithubRepository
    private lateinit var repositories: List<Repository>
    
    @Before
    fun setup() {
        mockRepository = mock()
        getRepositoriesUseCase = GetRepositoriesUseCase(mockRepository)
        
        repositories = listOf(
            Repository(
                id = 1,
                name = "repo1",
                description = "Repository 1",
                language = "Kotlin",
                stars = 100,
                forks = 50,
                openIssuesCount = 10,
                htmlUrl = "https://github.com/test/repo1",
                createdAt = "2021-01-01",
                updatedAt = "2021-02-01",
                topics = listOf("kotlin", "android")
            ),
            Repository(
                id = 2,
                name = "repo2",
                description = "Repository 2",
                language = "Java",
                stars = 200,
                forks = 100,
                openIssuesCount = 20,
                htmlUrl = "https://github.com/test/repo2",
                createdAt = "2021-01-02",
                updatedAt = "2021-02-02",
                topics = listOf("java", "android")
            )
        )
    }
    
    @Test
    fun `when repository returns data successfully, emit success result`() = runTest {
        // Given
        whenever(mockRepository.getRepositories()).thenReturn(repositories)
        
        // When
        val result = getRepositoriesUseCase().first()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(repositories, (result as Result.Success).data)
    }
    
    @Test
    fun `when repository throws exception, emit failure result`() = runTest {
        // Given
        val exception = RuntimeException("Error fetching repositories")
        whenever(mockRepository.getRepositories()).thenThrow(exception)
        
        // When
        val result = getRepositoriesUseCase().first()
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }
} 