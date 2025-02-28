package com.example.sa.domain.usecase

import com.example.sa.domain.model.Repository
import com.example.sa.ui.screens.repositories.FilterOption
import javax.inject.Inject

/**
 * Use case for filtering repositories by language or topic
 */
class FilterRepositoriesUseCase @Inject constructor() {
    
    operator fun invoke(repositories: List<Repository>, filterOption: FilterOption): List<Repository> {
        return when (filterOption) {
            FilterOption.ALL -> repositories
            FilterOption.JAVA -> repositories.filter { 
                it.language?.equals("Java", ignoreCase = true) == true 
            }
            FilterOption.KOTLIN -> repositories.filter { 
                it.language?.equals("Kotlin", ignoreCase = true) == true 
            }
            FilterOption.ANDROID -> repositories.filter { 
                it.topics.any { topic -> 
                    topic.equals("android", ignoreCase = true) 
                }
            }
        }
    }
} 