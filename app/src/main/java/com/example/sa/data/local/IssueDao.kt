package com.example.sa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {
    @Query("SELECT * FROM issues WHERE repositoryOwner = :owner AND repositoryName = :repo AND state = :state")
    fun getIssues(owner: String, repo: String, state: String): Flow<List<IssueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssues(issues: List<IssueEntity>)

    @Query("DELETE FROM issues WHERE repositoryOwner = :owner AND repositoryName = :repo AND state = :state")
    suspend fun deleteIssues(owner: String, repo: String, state: String)
} 