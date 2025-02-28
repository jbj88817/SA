package com.example.sa.di

import android.content.Context
import androidx.room.Room
import com.example.sa.data.local.AppDatabase
import com.example.sa.data.local.IssueDao
import com.example.sa.data.local.RepositoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "intuit_repos_db"
        ).build()
    }

    @Provides
    fun provideRepositoryDao(database: AppDatabase): RepositoryDao {
        return database.repositoryDao()
    }

    @Provides
    fun provideIssueDao(database: AppDatabase): IssueDao {
        return database.issueDao()
    }
} 