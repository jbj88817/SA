package com.example.sa.feature.repositories

import com.example.sa.domain.model.RepositoryFilter
import com.example.sa.domain.model.RepositorySort

internal fun RepositorySort.labelResId(): Int {
    return when (this) {
        RepositorySort.NAME_ASC -> R.string.sort_name_asc
        RepositorySort.NAME_DESC -> R.string.sort_name_desc
        RepositorySort.STARS_ASC -> R.string.sort_stars_asc
        RepositorySort.STARS_DESC -> R.string.sort_stars_desc
        RepositorySort.UPDATED_ASC -> R.string.sort_updated_asc
        RepositorySort.UPDATED_DESC -> R.string.sort_updated_desc
    }
}

internal fun RepositoryFilter.labelResId(): Int {
    return when (this) {
        RepositoryFilter.ALL -> R.string.filter_all
        RepositoryFilter.JAVA -> R.string.filter_java
        RepositoryFilter.KOTLIN -> R.string.filter_kotlin
        RepositoryFilter.ANDROID -> R.string.filter_android
    }
}

