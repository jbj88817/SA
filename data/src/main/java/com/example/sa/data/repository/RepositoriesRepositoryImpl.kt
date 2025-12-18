package com.example.sa.data.repository

import com.example.sa.data.local.RepositoryDao
import com.example.sa.data.local.RepositoryEntity
import com.example.sa.data.remote.GithubApiService
import com.example.sa.domain.model.Repository
import com.example.sa.domain.model.Result
import com.example.sa.domain.repository.RepositoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoriesRepositoryImpl @Inject constructor(
    private val repositoryDao: RepositoryDao,
    private val githubApiService: GithubApiService
) : RepositoriesRepository {

    override fun getRepositories(organization: String): Flow<Result<List<Repository>>> {
        return repositoryDao.getRepositories(organization)
            .map { entities ->
                Result.success(entities.map { it.toDomainModel() })
            }
            .catch { e ->
                emit(Result.error(e))
            }
    }

    override fun getRepository(organization: String, repositoryName: String): Flow<Result<Repository>> {
        return repositoryDao.getRepository(organization, repositoryName)
            .map { entity ->
                if (entity != null) {
                    Result.success(entity.toDomainModel())
                } else {
                    Result.error(Exception("Repository not found"))
                }
            }
            .catch { e ->
                emit(Result.error(e))
            }
    }

    override suspend fun refreshRepositories(organization: String) {
        val repositories = githubApiService.getRepositories(organization)
        val entities = repositories.map {
            RepositoryEntity.fromDomainModel(it.toDomainModel(), organization)
        }
        repositoryDao.deleteRepositoriesByOrganization(organization)
        repositoryDao.insertRepositories(entities)
    }

    override suspend fun refreshRepository(organization: String, repositoryName: String) {
        val repository = githubApiService.getRepository(organization, repositoryName)
        val entity = RepositoryEntity.fromDomainModel(repository.toDomainModel(), organization)
        repositoryDao.insertRepositories(listOf(entity))
    }
}
