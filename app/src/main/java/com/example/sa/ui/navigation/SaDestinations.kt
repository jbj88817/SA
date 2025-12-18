package com.example.sa.ui.navigation

import android.net.Uri

object SaDestinations {
    const val RepositoriesRoute = "repositories"

    const val RepositoryNameArg = "repositoryName"
    const val RepositoryDetailRoute = "repository_detail/{$RepositoryNameArg}"

    fun repositoryDetailRoute(repositoryName: String): String {
        return "repository_detail/${Uri.encode(repositoryName)}"
    }
}

