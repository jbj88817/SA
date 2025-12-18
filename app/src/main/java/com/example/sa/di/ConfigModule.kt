package com.example.sa.di

import com.example.sa.BuildConfig
import com.example.sa.domain.di.GithubBaseUrl
import com.example.sa.domain.di.GithubOrg
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    @GithubBaseUrl
    fun provideGithubBaseUrl(): String = BuildConfig.GITHUB_BASE_URL

    @Provides
    @Singleton
    @GithubOrg
    fun provideGithubOrg(): String = BuildConfig.GITHUB_ORG
}
