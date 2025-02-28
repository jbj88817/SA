package com.example.sa.di

import com.example.sa.data.local.IssueDao
import com.example.sa.data.remote.GithubApiService
import com.example.sa.data.repository.GithubRepositoryImpl
import com.example.sa.data.repository.IssueRepositoryImpl
import com.example.sa.domain.repository.GithubRepository
import com.example.sa.domain.repository.IssueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideGithubRepository(
        apiService: GithubApiService
    ): GithubRepository {
        return GithubRepositoryImpl(apiService)
    }
    
    @Provides
    @Singleton
    fun provideIssueRepository(
        issueDao: IssueDao,
        apiService: GithubApiService
    ): IssueRepository {
        return IssueRepositoryImpl(issueDao, apiService)
    }
} 