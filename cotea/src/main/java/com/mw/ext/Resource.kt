package com.mw.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Resource<out T> {

    data object Loading : Resource<Nothing>()
    data class Data<out T>(val value: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()
}

fun <T> Flow<T>.asResource(): Flow<Resource<T>> = flow {
    onStart { emit(Resource.Loading) }
        .map { data -> Resource.Data(data) }
        .catch { throwable -> emit(Resource.Error(throwable)) }
        .collect(::emit)
}