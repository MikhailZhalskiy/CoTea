package com.mw.ext

data class ReloadableData<T>(val value: T?, val status: Status) {
    sealed class Status {
        data object Idle: Status()
        data object Loading: Status()
        data class Error(val throwable: Throwable): Status()
    }

    fun isInitial(): Boolean {
        return value == null && status == Status.Idle
    }

    fun isSuccess(): Boolean {
        return value != null && status == Status.Idle
    }
}