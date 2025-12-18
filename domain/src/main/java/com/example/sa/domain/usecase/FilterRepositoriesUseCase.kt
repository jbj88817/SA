package com.example.sa.domain.usecase

import com.example.sa.domain.model.RepositoryFilter
import com.example.sa.domain.model.Repository
import javax.inject.Inject

/**
 * Use case for filtering repositories by language or topic
 */
class FilterRepositoriesUseCase @Inject constructor() {
    
    operator fun invoke(repositories: List<Repository>, filter: RepositoryFilter): List<Repository> {
        return when (filter) {
            RepositoryFilter.ALL -> repositories
            RepositoryFilter.JAVA -> repositories.filter { 
                it.language?.equals("Java", ignoreCase = true) == true 
            }
            RepositoryFilter.KOTLIN -> repositories.filter { 
                it.language?.equals("Kotlin", ignoreCase = true) == true 
            }
            RepositoryFilter.ANDROID -> repositories.filter { 
                it.topics.any { topic -> 
                    topic.equals("android", ignoreCase = true) 
                }
            }
        }
    }
}
