package com.example.sa.domain.model

data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val body: String?,
    val createdAt: String,
    val updatedAt: String,
    val htmlUrl: String,
    val user: User
)

data class User(
    val id: Long,
    val login: String,
    val avatarUrl: String
) 