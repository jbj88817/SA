package com.example.intuitrepos.data.repository

import com.example.intuitrepos.data.local.IssueDao
import com.example.intuitrepos.data.local.IssueEntity
import com.example.intuitrepos.data.remote.GithubApiService
import com.example.intuitrepos.domain.model.Issue
import com.example.intuitrepos.domain.model.Result
import com.example.intuitrepos.domain.repository.IssueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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