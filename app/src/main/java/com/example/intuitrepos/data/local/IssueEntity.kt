package com.example.intuitrepos.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.intuitrepos.domain.model.Issue
import com.example.intuitrepos.domain.model.User

@Entity(tableName = "issues")
data class IssueEntity(
    @PrimaryKey
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val body: String?,
    val createdAt: String,
    val updatedAt: String,
    val htmlUrl: String,
    @Embedded(prefix = "user_")
    val user: UserEntity,
    val repositoryOwner: String,
    val repositoryName: String
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

    companion object {
        fun fromDomainModel(
            issue: Issue,
            repositoryOwner: String,
            repositoryName: String
        ): IssueEntity {
            return IssueEntity(
                id = issue.id,
                number = issue.number,
                title = issue.title,
                state = issue.state,
                body = issue.body,
                createdAt = issue.createdAt,
                updatedAt = issue.updatedAt,
                htmlUrl = issue.htmlUrl,
                user = UserEntity.fromDomainModel(issue.user),
                repositoryOwner = repositoryOwner,
                repositoryName = repositoryName
            )
        }
    }
}

data class UserEntity(
    val id: Long,
    val login: String,
    val avatarUrl: String
) {
    fun toDomainModel(): User {
        return User(
            id = id,
            login = login,
            avatarUrl = avatarUrl
        )
    }

    companion object {
        fun fromDomainModel(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                login = user.login,
                avatarUrl = user.avatarUrl
            )
        }
    }
} 