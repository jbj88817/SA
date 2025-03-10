package com.example.sa.domain.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(exception: Throwable): Result<Nothing> = Error(exception)
        fun <T> loading(): Result<T> = Loading
        fun <T> failure(exception: Throwable): Result<T> = Error(exception)
    }
    
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (Throwable) -> R
    ): R {
        return when (this) {
            is Success -> onSuccess(data)
            is Error -> onFailure(exception)
            is Loading -> onFailure(IllegalStateException("Result is still loading"))
        }
    }
} 