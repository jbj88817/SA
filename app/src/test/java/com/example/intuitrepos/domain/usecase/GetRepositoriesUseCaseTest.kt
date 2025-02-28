package com.example.intuitrepos.domain.usecase

import com.example.intuitrepos.domain.model.Repository
import com.example.intuitrepos.domain.model.Result
import com.example.intuitrepos.domain.repository.RepositoryRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetRepositoriesUseCaseTest {

    private lateinit var repositoryRepository: RepositoryRepository
    private lateinit var getRepositoriesUseCase: GetRepositoriesUseCase

    @Before
    fun setUp() {
        repositoryRepository = mock()
        getRepositoriesUseCase = GetRepositoriesUseCase(repositoryRepository)
    }

    @Test
    fun `invoke returns repositories from repository`() = runTest {
        // Given
        val organization = "intuit"
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
            ),
            Repository(
                id = 2,
                name = "repo2",
                description = "description2",
                language = "Java",
                stargazersCount = 200,
                forksCount = 100,
                openIssuesCount = 20,
                htmlUrl = "https://github.com/intuit/repo2",
                createdAt = "2021-02-01",
                updatedAt = "2021-02-02"
            )
        )
        val expectedResult = Result.Success(repositories)
        
        whenever(repositoryRepository.getRepositories(organization))
            .thenReturn(flowOf(expectedResult))

        // When
        val result = getRepositoriesUseCase(organization).single()

        // Then
        assertEquals(expectedResult, result)
    }
} 