package com.example.sa.data.repository

import com.example.sa.data.remote.GithubApiService
import com.example.sa.data.remote.RepositoryDto // Assuming this is the correct import path
import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

class RepositoryRepositoryImplTest {

    private lateinit var githubApiService: GithubApiService
    private lateinit var repositoryRepository: RepositoryRepositoryImpl // This is the class under test

    @Before
    fun setUp() {
        githubApiService = mock()
        // As per the subtask description, RepositoryRepositoryImpl only takes GithubApiService
        repositoryRepository = RepositoryRepositoryImpl(githubApiService)
    }

    @Test
    fun `getRepositories returns success with mapped domain models when apiService succeeds`() = runTest {
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
                updatedAt = "2021-01-02",
                topics = listOf("android", "kotlin")
            ),
            RepositoryDto(
                id = 2,
                name = "repo2",
                description = "description2",
                language = "Java",
                stargazersCount = 200,
                forksCount = 100,
                openIssuesCount = 20,
                htmlUrl = "https://github.com/intuit/repo2",
                createdAt = "2021-02-01",
                updatedAt = "2021-02-02",
                topics = listOf("java")
            )
        )
        // Assuming RepositoryDto has a toDomainModel() method or an extension function
        val expectedDomainRepositories = repositoryDtos.map { it.toDomainModel() }

        whenever(githubApiService.getRepositories(organization)).thenReturn(repositoryDtos)

        // When
        val result = repositoryRepository.getRepositories(organization).first()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedDomainRepositories, (result as Result.Success).data)
    }

    @Test
    fun `getRepositories returns error when apiService throws exception`() = runTest {
        // Given
        val organization = "intuit"
        val expectedException = IOException("Network error")
        whenever(githubApiService.getRepositories(organization)).thenThrow(expectedException)

        // When
        val result = repositoryRepository.getRepositories(organization).first()

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }

    @Test
    fun `getRepository returns success with mapped domain model when apiService succeeds`() = runTest {
        // Given
        val organization = "intuit"
        val repoName = "repo1"
        val repositoryDto = RepositoryDto(
            id = 1,
            name = repoName,
            description = "description1",
            language = "Kotlin",
            stargazersCount = 100,
            forksCount = 50,
            openIssuesCount = 10,
            htmlUrl = "https://github.com/intuit/repo1",
            createdAt = "2021-01-01",
            updatedAt = "2021-01-02",
            topics = listOf("android", "kotlin")
        )
        val expectedDomainRepository = repositoryDto.toDomainModel()

        whenever(githubApiService.getRepository(organization, repoName)).thenReturn(repositoryDto)

        // When
        val result = repositoryRepository.getRepository(organization, repoName).first()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedDomainRepository, (result as Result.Success).data)
    }

    @Test
    fun `getRepository returns error when apiService throws exception`() = runTest {
        // Given
        val organization = "intuit"
        val repoName = "repo1"
        val expectedException = RuntimeException("API error / Not Found") 
        whenever(githubApiService.getRepository(organization, repoName)).thenThrow(expectedException)

        // When
        val result = repositoryRepository.getRepository(organization, repoName).first()

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }
}
