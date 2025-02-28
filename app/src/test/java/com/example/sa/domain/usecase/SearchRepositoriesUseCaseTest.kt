package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchRepositoriesUseCaseTest {
    
    private lateinit var searchRepositoriesUseCase: SearchRepositoriesUseCase
    private lateinit var repositories: List<Repository>
    
    @Before
    fun setup() {
        searchRepositoriesUseCase = SearchRepositoriesUseCase()
        
        repositories = listOf(
            Repository(
                id = 1,
                name = "android-app",
                description = "Android application",
                language = "Kotlin",
                stars = 100,
                forks = 50,
                openIssuesCount = 10,
                htmlUrl = "https://github.com/test/android-app",
                createdAt = "2021-01-01",
                updatedAt = "2021-02-01",
                topics = listOf("android", "kotlin")
            ),
            Repository(
                id = 2,
                name = "ios-app",
                description = "iOS application",
                language = "Swift",
                stars = 200,
                forks = 100,
                openIssuesCount = 20,
                htmlUrl = "https://github.com/test/ios-app",
                createdAt = "2021-01-02",
                updatedAt = "2021-02-02",
                topics = listOf("ios", "swift")
            ),
            Repository(
                id = 3,
                name = "web-app",
                description = "Web application",
                language = "JavaScript",
                stars = 300,
                forks = 150,
                openIssuesCount = 30,
                htmlUrl = "https://github.com/test/web-app",
                createdAt = "2021-01-03",
                updatedAt = "2021-02-03",
                topics = listOf("web", "javascript")
            )
        )
    }
    
    @Test
    fun `when query is empty, return all repositories`() {
        val result = searchRepositoriesUseCase(repositories, "")
        assertEquals(repositories, result)
    }
    
    @Test
    fun `when query matches repository name, return matching repositories`() {
        val result = searchRepositoriesUseCase(repositories, "android")
        assertEquals(1, result.size)
        assertEquals("android-app", result[0].name)
    }
    
    @Test
    fun `when query matches repository description, return matching repositories`() {
        val result = searchRepositoriesUseCase(repositories, "web application")
        assertEquals(1, result.size)
        assertEquals("web-app", result[0].name)
    }
    
    @Test
    fun `when query is case insensitive, return matching repositories`() {
        val result = searchRepositoriesUseCase(repositories, "ANDROID")
        assertEquals(1, result.size)
        assertEquals("android-app", result[0].name)
    }
    
    @Test
    fun `when query matches multiple repositories, return all matching repositories`() {
        val result = searchRepositoriesUseCase(repositories, "app")
        assertEquals(3, result.size)
    }
    
    @Test
    fun `when query does not match any repository, return empty list`() {
        val result = searchRepositoriesUseCase(repositories, "nonexistent")
        assertEquals(0, result.size)
    }
} 