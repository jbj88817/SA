package com.example.sa.di

import com.example.sa.data.repository.IssueRepositoryImpl
import com.example.sa.data.repository.RepositoryRepositoryImpl
import com.example.sa.domain.repository.IssueRepository
import com.example.sa.domain.repository.RepositoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepositoryRepository(
        repositoryRepositoryImpl: RepositoryRepositoryImpl
    ): RepositoryRepository

    @Binds
    @Singleton
    abstract fun bindIssueRepository(
        issueRepositoryImpl: IssueRepositoryImpl
    ): IssueRepository
} 