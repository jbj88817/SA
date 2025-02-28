package com.example.sa.data.repository

import com.example.sa.data.local.IssueDao
import com.example.sa.data.local.IssueEntity
import com.example.sa.data.remote.GithubApiService
import com.example.sa.domain.model.Issue
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.IssueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRepositoryImpl @Inject constructor(
    private val issueDao: IssueDao,
    private val githubApiService: GithubApiService
) : IssueRepository {

    override fun getIssues(owner: String, repo: String, state: String): Flow<Result<List<Issue>>> {
        return issueDao.getIssues(owner, repo, state)
            .map { entities ->
                Result.success(entities.map { it.toDomainModel() })
            }
            .onStart { 
                // Refresh issues from API when flow starts collecting
                try {
                    refreshIssues(owner, repo, state)
                } catch (e: Exception) {
                    // If refresh fails, we'll still emit from the database
                    // but we won't emit an error here
                }
            }
            .catch { e ->
                emit(Result.error(e))
            }
    }

    override suspend fun refreshIssues(owner: String, repo: String, state: String) {
        try {
            val issues = githubApiService.getIssues(owner, repo, state)
            val entities = issues.map { 
                IssueEntity.fromDomainModel(it.toDomainModel(), owner, repo) 
            }
            issueDao.deleteIssues(owner, repo, state)
            issueDao.insertIssues(entities)
        } catch (e: Exception) {
            throw e
        }
    }
} 