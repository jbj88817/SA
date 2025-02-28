package com.example.intuitrepos.domain.usecase

import com.example.intuitrepos.domain.repository.IssueRepository
import javax.inject.Inject

class RefreshIssuesUseCase @Inject constructor(
    private val repository: IssueRepository
) {
    suspend operator fun invoke(owner: String, repo: String, state: String) {
        repository.refreshIssues(owner, repo, state)
    }
} 