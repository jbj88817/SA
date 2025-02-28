package com.example.sa.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    @GET("orgs/{org}/repos")
    suspend fun getRepositories(
        @Path("org") organization: String,
        @Query("per_page") perPage: Int = 100
    ): List<RepositoryDto>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepositoryDto

    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String,
        @Query("per_page") perPage: Int = 100
    ): List<IssueDto>
} 