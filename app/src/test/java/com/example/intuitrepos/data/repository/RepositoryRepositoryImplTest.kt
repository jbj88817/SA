package com.example.intuitrepos.data.repository

import com.example.intuitrepos.data.local.RepositoryDao
import com.example.intuitrepos.data.local.RepositoryEntity
import com.example.intuitrepos.data.remote.GithubApiService
import com.example.intuitrepos.data.remote.RepositoryDto
import com.example.intuitrepos.domain.model.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RepositoryRepositoryImplTest {

    private lateinit var repositoryDao: RepositoryDao
    private lateinit var githubApiService: GithubApiService
    private lateinit var repositoryRepository: RepositoryRepositoryImpl

    @Before
    fun setUp() {
        repositoryDao = mock()
        githubApiService = mock()
        repositoryRepository = RepositoryRepositoryImpl(repositoryDao, githubApiService)
    }

    @Test
    fun `getRepositories returns repositories from local database`() = runTest {
        // Given
        val organization = "intuit"
        val repositoryEntities = listOf(
            RepositoryEntity(
                id = 1,
                name = "repo1",
                description = "description1",
                language = "Kotlin",
                stargazersCount = 100,
                forksCount = 50,
                openIssuesCount = 10,
                htmlUrl = "https://github.com/intuit/repo1",
                createdAt = "2021-01-01",
                updatedAt = "2021-01-02",
                organization = organization
            )
        )
        
        whenever(repositoryDao.getRepositories(organization))
            .thenReturn(flowOf(repositoryEntities))

        // When
        val result = repositoryRepository.getRepositories(organization).first()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.size)
        assertEquals("repo1", result.data[0].name)
    }

    @Test
    fun `refreshRepositories fetches repositories from API and saves to database`() = runTest {
        // Given
        val organization = "intuit"
        val repositoryDtos = listOf(
            RepositoryDto(
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
        
        whenever(githubApiService.getRepositories(organization))
            .thenReturn(repositoryDtos)

        // When
        repositoryRepository.refreshRepositories(organization)

        // Then
        verify(repositoryDao).deleteRepositoriesByOrganization(organization)
        verify(repositoryDao).insertRepositories(any())
    }

    @Test
    fun `getRepository returns repository from local database`() = runTest {
        // Given
        val organization = "intuit"
        val repositoryName = "repo1"
        val repositoryEntity = RepositoryEntity(
            id = 1,
            name = repositoryName,
            description = "description1",
            language = "Kotlin",
            stargazersCount = 100,
            forksCount = 50,
            openIssuesCount = 10,
            htmlUrl = "https://github.com/intuit/repo1",
            createdAt = "2021-01-01",
            updatedAt = "2021-01-02",
            organization = organization
        )
        
        whenever(repositoryDao.getRepository(organization, repositoryName))
            .thenReturn(flowOf(repositoryEntity))

        // When
        val result = repositoryRepository.getRepository(organization, repositoryName).first()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(repositoryName, (result as Result.Success).data.name)
    }

    @Test
    fun `getRepository returns error when repository not found`() = runTest {
        // Given
        val organization = "intuit"
        val repositoryName = "non-existent-repo"
        
        whenever(repositoryDao.getRepository(organization, repositoryName))
            .thenReturn(flowOf(null))

        // When
        val result = repositoryRepository.getRepository(organization, repositoryName).first()

        // Then
        assertTrue(result is Result.Error)
    }
} 