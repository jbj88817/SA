package com.example.sa.domain.usecase

import com.example.sa.domain.model.Issue
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.IssueRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting issues for a repository
 * Note: This is a mock implementation since we're focusing on repositories
 */
class GetIssuesUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    
    operator fun invoke(owner: String, repo: String, state: String): Flow<Result<List<Issue>>> {
        // Simply pass through the flow from the repository
        return issueRepository.getIssues(owner, repo, state)
    }
} 