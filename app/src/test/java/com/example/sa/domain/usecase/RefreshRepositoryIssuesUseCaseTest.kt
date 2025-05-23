package com.example.sa.domain.usecase

import com.example.sa.domain.repository.IssueRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RefreshRepositoryIssuesUseCaseTest {

    private lateinit var issueRepository: IssueRepository
    private lateinit var refreshRepositoryIssuesUseCase: RefreshRepositoryIssuesUseCase

    @Before
    fun setUp() {
        issueRepository = mock()
        refreshRepositoryIssuesUseCase = RefreshRepositoryIssuesUseCase(issueRepository)
    }

    @Test
    fun `invoke should call refreshIssues on IssueRepository with correct parameters`() = runTest {
        // Given
        val owner = "intuit"
        val repo = "quickbooks"
        val state = "open" // Example state, can be any relevant string

        // When
        refreshRepositoryIssuesUseCase.invoke(owner, repo, state)

        // Then
        // Verify that issueRepository.refreshIssues was called with the exact parameters
        verify(issueRepository).refreshIssues(owner, repo, state)
    }
}
