package com.example.intuitrepos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.intuitrepos.domain.model.Repository

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val htmlUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val organization: String
) {
    fun toDomainModel(): Repository {
        return Repository(
            id = id,
            name = name,
            description = description,
            language = language,
            stargazersCount = stargazersCount,
            forksCount = forksCount,
            openIssuesCount = openIssuesCount,
            htmlUrl = htmlUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomainModel(repository: Repository, organization: String): RepositoryEntity {
            return RepositoryEntity(
                id = repository.id,
                name = repository.name,
                description = repository.description,
                language = repository.language,
                stargazersCount = repository.stargazersCount,
                forksCount = repository.forksCount,
                openIssuesCount = repository.openIssuesCount,
                htmlUrl = repository.htmlUrl,
                createdAt = repository.createdAt,
                updatedAt = repository.updatedAt,
                organization = organization
            )
        }
    }
} 