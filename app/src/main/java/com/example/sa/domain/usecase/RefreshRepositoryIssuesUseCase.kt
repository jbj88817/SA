package com.example.sa.domain.usecase

import com.example.sa.domain.repository.IssueRepository
import javax.inject.Inject

class RefreshRepositoryIssuesUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(owner: String, repo: String, state: String) {
        issueRepository.refreshIssues(owner, repo, state)
    }
} 