package com.example.intuitrepos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RepositoryDao {
    @Query("SELECT * FROM repositories WHERE organization = :organization")
    fun getRepositories(organization: String): Flow<List<RepositoryEntity>>

    @Query("SELECT * FROM repositories WHERE organization = :organization AND name = :repositoryName LIMIT 1")
    fun getRepository(organization: String, repositoryName: String): Flow<RepositoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @Query("DELETE FROM repositories WHERE organization = :organization")
    suspend fun deleteRepositoriesByOrganization(organization: String)
} 