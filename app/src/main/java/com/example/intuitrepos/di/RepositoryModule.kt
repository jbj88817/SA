package com.example.intuitrepos.di

import com.example.intuitrepos.data.repository.IssueRepositoryImpl
import com.example.intuitrepos.data.repository.RepositoryRepositoryImpl
import com.example.intuitrepos.domain.repository.IssueRepository
import com.example.intuitrepos.domain.repository.RepositoryRepository
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