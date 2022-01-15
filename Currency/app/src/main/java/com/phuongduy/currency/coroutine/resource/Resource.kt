package com.phuongduy.currency.coroutine.resource

import com.phuongduy.currency.coroutine.executeForResult

sealed class Resource<T : Any> {
    data class Success<T : Any>(val data: T) : Resource<T>()
    data class Error<T : Any>(val error: Throwable) : Resource<T>()

    suspend fun <O : Any> flatMap(function: suspend (T) -> Resource<O>): Resource<O> {
        return when (this) {
            is Success -> function(this.data)
            is Error -> Error(this.error)
        }
    }

    suspend fun <O : Any> zipWith(function: suspend (T) -> Resource<O>): Resource<Pair<T, O>> {
        return when (this) {
            is Success -> function(data).map {
                Pair(data, it)
            }
            is Error -> Error(this.error)
        }
    }

    suspend fun <O : Any> map(function: suspend (T) -> O): Resource<O> {
        return when (this) {
            is Success -> executeForResult {
                function(this.data)
            }
            is Error -> Error(error)
        }
    }

    suspend fun doOnSuccess(function: suspend (T) -> Unit): Resource<T> {
        if (this is Success) {
            function(this.data)
        }
        return this
    }

    companion object {
        fun <T : Any> success(data: T): Success<T> {
            return Success(data)
        }

        fun <T : Any> error(error: Throwable): Error<T> {
            return Error(error)
        }
    }
}

suspend fun <T : Any> Resource<T>.onErrorResume(function: suspend (Throwable) -> Resource<T>): Resource<T> {
    return when (this) {
        is Resource.Success -> this
        is Resource.Error -> function(this.error)
    }
}
