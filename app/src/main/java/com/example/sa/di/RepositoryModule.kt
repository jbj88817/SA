package com.example.sa.di

import com.example.sa.data.repository.IssueRepositoryImpl
import com.example.sa.domain.repository.IssueRepository
import com.example.sa.data.repository.RepositoriesRepositoryImpl
import com.example.sa.domain.repository.RepositoriesRepository
import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepositoriesRepository(
        impl: RepositoriesRepositoryImpl
    ): RepositoriesRepository

    @Binds
    @Singleton
    abstract fun bindIssueRepository(
        impl: IssueRepositoryImpl
    ): IssueRepository
}
