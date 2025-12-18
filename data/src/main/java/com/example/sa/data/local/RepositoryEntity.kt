package com.example.sa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sa.domain.model.Repository

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val openIssuesCount: Int,
    val htmlUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val organization: String,
    val topics: String = "" // Store as comma-separated string
) {
    fun toDomainModel(): Repository {
        return Repository(
            id = id,
            name = name,
            description = description,
            language = language,
            stars = stars,
            forks = forks,
            openIssuesCount = openIssuesCount,
            htmlUrl = htmlUrl,
            createdAt = createdAt,
            updatedAt = updatedAt,
            topics = topics.split(",").filter { it.isNotEmpty() }
        )
    }

    companion object {
        fun fromDomainModel(repository: Repository, organization: String): RepositoryEntity {
            return RepositoryEntity(
                id = repository.id,
                name = repository.name,
                description = repository.description,
                language = repository.language,
                stars = repository.stars,
                forks = repository.forks,
                openIssuesCount = repository.openIssuesCount,
                htmlUrl = repository.htmlUrl,
                createdAt = repository.createdAt,
                updatedAt = repository.updatedAt,
                organization = organization,
                topics = repository.topics.joinToString(",")
            )
        }
    }
} 