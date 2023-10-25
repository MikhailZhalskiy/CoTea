package com.mw.cotea.util

import androidx.annotation.IntDef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Resource<out T> {

    @State
    abstract val state: Int

    object Loading : Resource<Nothing>() {
        @State
        override val state: Int = STATE_LOADING
    }

    data class Data<out T>(val value: T) : Resource<T>() {
        @State
        override val state: Int = STATE_SUCCESS
    }

    data class Error(val throwable: Throwable) : Resource<Nothing>() {
        @State
        override val state: Int = STATE_ERROR
    }

    companion object {

        const val STATE_LOADING = 1
        const val STATE_SUCCESS = 1 shl 1
        const val STATE_ERROR = 1 shl 2

        @IntDef(STATE_LOADING, STATE_SUCCESS, STATE_ERROR)
        annotation class State
    }
}

fun <T> Flow<T>.asResource(): Flow<Resource<T>> = flow {
    onStart { emit(Resource.Loading) }
        .map { data -> Resource.Data(data) }
        .catch { throwable -> emit(Resource.Error(throwable)) }
        .collect(::emit)
}