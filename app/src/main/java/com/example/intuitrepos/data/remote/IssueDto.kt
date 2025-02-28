package com.example.intuitrepos.data.remote

import com.example.intuitrepos.domain.model.Issue
import com.example.intuitrepos.domain.model.User
import com.google.gson.annotations.SerializedName

data class IssueDto(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val body: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    val user: UserDto
) {
    fun toDomainModel(): Issue {
        return Issue(
            id = id,
            number = number,
            title = title,
            state = state,
            body = body,
            createdAt = createdAt,
            updatedAt = updatedAt,
            htmlUrl = htmlUrl,
            user = user.toDomainModel()
        )
    }
}

data class UserDto(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
) {
    fun toDomainModel(): User {
        return User(
            id = id,
            login = login,
            avatarUrl = avatarUrl
        )
    }
} 