package com.phuongduy.currency.coroutine.resource

sealed class CompletableResource {
    object Completed : CompletableResource()
    data class Failed(val error: Throwable) : CompletableResource()
}
