package com.mw.cotea.util

data class ReloadableData<T>(val value: T?, val status: Status) {
    sealed class Status {
        object Idle: Status()
        object Loading: Status()
        data class Error(val throwable: Throwable): Status()
    }

    fun isInitial(): Boolean {
        return value == null && status == Status.Idle
    }

    fun isSuccess(): Boolean {
        return value != null && status == Status.Idle
    }
}