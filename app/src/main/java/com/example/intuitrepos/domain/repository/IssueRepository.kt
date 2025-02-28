package com.example.intuitrepos.domain.repository

import com.example.intuitrepos.domain.model.Issue
import com.example.intuitrepos.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface IssueRepository {
    fun getIssues(owner: String, repo: String, state: String): Flow<Result<List<Issue>>>
    suspend fun refreshIssues(owner: String, repo: String, state: String)
} 