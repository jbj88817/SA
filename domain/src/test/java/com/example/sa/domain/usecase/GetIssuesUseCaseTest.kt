package com.example.sa.domain.usecase

import com.example.sa.domain.model.Issue
import com.example.sa.domain.model.Result
import com.example.sa.domain.model.User
import com.example.sa.domain.repository.IssueRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetIssuesUseCaseTest {

    private lateinit var issueRepository: IssueRepository
    private lateinit var getIssuesUseCase: GetIssuesUseCase

    @Before
    fun setUp() {
        issueRepository = mock()
        getIssuesUseCase = GetIssuesUseCase(issueRepository)
    }

    @Test
    fun `invoke returns issues from repository`() = runTest {
        // Given
        val owner = "intuit"
        val repo = "repo1"
        val state = "open"
        val issues = listOf(
            Issue(
                id = 1,
                number = 101,
                title = "Issue 1",
                state = "open",
                body = "Issue body 1",
                createdAt = "2021-01-01",
                updatedAt = "2021-01-02",
                htmlUrl = "https://github.com/intuit/repo1/issues/101",
                user = User(
                    id = 1001,
                    login = "user1",
                    avatarUrl = "https://github.com/user1.png"
                )
            ),
            Issue(
                id = 2,
                number = 102,
                title = "Issue 2",
                state = "open",
                body = "Issue body 2",
                createdAt = "2021-02-01",
                updatedAt = "2021-02-02",
                htmlUrl = "https://github.com/intuit/repo1/issues/102",
                user = User(
                    id = 1002,
                    login = "user2",
                    avatarUrl = "https://github.com/user2.png"
                )
            )
        )
        val expectedResult = Result.Success(issues)
        
        whenever(issueRepository.getIssues(owner, repo, state))
            .thenReturn(flowOf(expectedResult))

        // When
        val result = getIssuesUseCase(owner, repo, state).single()

        // Then
        assertEquals(expectedResult, result)
    }
} 