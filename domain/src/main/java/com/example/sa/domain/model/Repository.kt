package com.example.sa.domain.model

data class Repository(
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
    val topics: List<String> = emptyList()
) 