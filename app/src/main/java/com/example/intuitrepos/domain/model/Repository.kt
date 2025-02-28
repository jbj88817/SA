package com.example.intuitrepos.domain.model

data class Repository(
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val htmlUrl: String,
    val createdAt: String,
    val updatedAt: String
) 