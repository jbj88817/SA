package com.example.intuitrepos.domain.usecase

import com.example.intuitrepos.domain.model.Issue
import com.example.intuitrepos.domain.model.Result
import com.example.intuitrepos.domain.repository.IssueRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIssuesUseCase @Inject constructor(
    private val repository: IssueRepository
) {
    operator fun invoke(owner: String, repo: String, state: String): Flow<Result<List<Issue>>> {
        return repository.getIssues(owner, repo, state)
    }
} 