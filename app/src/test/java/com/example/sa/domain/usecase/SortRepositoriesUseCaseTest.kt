package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.ui.screens.repositories.SortOption
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SortRepositoriesUseCaseTest {
    
    private lateinit var sortRepositoriesUseCase: SortRepositoriesUseCase
    private lateinit var repositories: List<Repository>
    
    @Before
    fun setup() {
        sortRepositoriesUseCase = SortRepositoriesUseCase()
        
        repositories = listOf(
            Repository(
                id = 1,
                name = "c-app",
                description = "C application",
                language = "C",
                stars = 100,
                forks = 50,
                openIssuesCount = 10,
                htmlUrl = "https://github.com/test/c-app",
                createdAt = "2021-01-01",
                updatedAt = "2021-03-01",
                topics = listOf("c", "app")
            ),
            Repository(
                id = 2,
                name = "b-app",
                description = "B application",
                language = "B",
                stars = 300,
                forks = 150,
                openIssuesCount = 30,
                htmlUrl = "https://github.com/test/b-app",
                createdAt = "2021-01-02",
                updatedAt = "2021-01-01",
                topics = listOf("b", "app")
            ),
            Repository(
                id = 3,
                name = "a-app",
                description = "A application",
                language = "A",
                stars = 200,
                forks = 100,
                openIssuesCount = 20,
                htmlUrl = "https://github.com/test/a-app",
                createdAt = "2021-01-03",
                updatedAt = "2021-02-01",
                topics = listOf("a", "app")
            )
        )
    }
    
    @Test
    fun `when sort is NAME_ASC, sort repositories by name in ascending order`() {
        val result = sortRepositoriesUseCase(repositories, SortOption.NAME_ASC)
        assertEquals("a-app", result[0].name)
        assertEquals("b-app", result[1].name)
        assertEquals("c-app", result[2].name)
    }
    
    @Test
    fun `when sort is NAME_DESC, sort repositories by name in descending order`() {
        val result = sortRepositoriesUseCase(repositories, SortOption.NAME_DESC)
        assertEquals("c-app", result[0].name)
        assertEquals("b-app", result[1].name)
        assertEquals("a-app", result[2].name)
    }
    
    @Test
    fun `when sort is STARS_ASC, sort repositories by stars in ascending order`() {
        val result = sortRepositoriesUseCase(repositories, SortOption.STARS_ASC)
        assertEquals(100, result[0].stars)
        assertEquals(200, result[1].stars)
        assertEquals(300, result[2].stars)
    }
    
    @Test
    fun `when sort is STARS_DESC, sort repositories by stars in descending order`() {
        val result = sortRepositoriesUseCase(repositories, SortOption.STARS_DESC)
        assertEquals(300, result[0].stars)
        assertEquals(200, result[1].stars)
        assertEquals(100, result[2].stars)
    }
    
    @Test
    fun `when sort is UPDATED_ASC, sort repositories by updated date in ascending order`() {
        val result = sortRepositoriesUseCase(repositories, SortOption.UPDATED_ASC)
        assertEquals("2021-01-01", result[0].updatedAt)
        assertEquals("2021-02-01", result[1].updatedAt)
        assertEquals("2021-03-01", result[2].updatedAt)
    }
    
    @Test
    fun `when sort is UPDATED_DESC, sort repositories by updated date in descending order`() {
        val result = sortRepositoriesUseCase(repositories, SortOption.UPDATED_DESC)
        assertEquals("2021-03-01", result[0].updatedAt)
        assertEquals("2021-02-01", result[1].updatedAt)
        assertEquals("2021-01-01", result[2].updatedAt)
    }
} 