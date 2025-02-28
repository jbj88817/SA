package com.example.sa.data.remote

import com.example.sa.domain.model.Repository
import com.google.gson.annotations.SerializedName

data class RepositoryDto(
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    @SerializedName("forks_count")
    val forksCount: Int,
    @SerializedName("open_issues_count")
    val openIssuesCount: Int,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
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
} 