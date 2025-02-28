package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.ui.screens.repositories.FilterOption
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterRepositoriesUseCaseTest {
    
    private lateinit var filterRepositoriesUseCase: FilterRepositoriesUseCase
    private lateinit var repositories: List<Repository>
    
    @Before
    fun setup() {
        filterRepositoriesUseCase = FilterRepositoriesUseCase()
        
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
                name = "java-lib",
                description = "Java library",
                language = "Java",
                stars = 200,
                forks = 100,
                openIssuesCount = 20,
                htmlUrl = "https://github.com/test/java-lib",
                createdAt = "2021-01-02",
                updatedAt = "2021-02-02",
                topics = listOf("java", "library")
            ),
            Repository(
                id = 3,
                name = "kotlin-lib",
                description = "Kotlin library",
                language = "Kotlin",
                stars = 300,
                forks = 150,
                openIssuesCount = 30,
                htmlUrl = "https://github.com/test/kotlin-lib",
                createdAt = "2021-01-03",
                updatedAt = "2021-02-03",
                topics = listOf("kotlin", "library")
            ),
            Repository(
                id = 4,
                name = "android-lib",
                description = "Android library",
                language = "Java",
                stars = 400,
                forks = 200,
                openIssuesCount = 40,
                htmlUrl = "https://github.com/test/android-lib",
                createdAt = "2021-01-04",
                updatedAt = "2021-02-04",
                topics = listOf("android", "library")
            )
        )
    }
    
    @Test
    fun `when filter is ALL, return all repositories`() {
        val result = filterRepositoriesUseCase(repositories, FilterOption.ALL)
        assertEquals(repositories, result)
    }
    
    @Test
    fun `when filter is JAVA, return only Java repositories`() {
        val result = filterRepositoriesUseCase(repositories, FilterOption.JAVA)
        assertEquals(2, result.size)
        assertEquals("Java", result[0].language)
        assertEquals("Java", result[1].language)
    }
    
    @Test
    fun `when filter is KOTLIN, return only Kotlin repositories`() {
        val result = filterRepositoriesUseCase(repositories, FilterOption.KOTLIN)
        assertEquals(2, result.size)
        assertEquals("Kotlin", result[0].language)
        assertEquals("Kotlin", result[1].language)
    }
    
    @Test
    fun `when filter is ANDROID, return only Android repositories`() {
        val result = filterRepositoriesUseCase(repositories, FilterOption.ANDROID)
        assertEquals(2, result.size)
        assertEquals(true, result[0].topics.contains("android"))
        assertEquals(true, result[1].topics.contains("android"))
    }
} 