package com.phuongduy.currency.coroutine

import com.phuongduy.currency.coroutine.resource.CompletableResource
import com.phuongduy.currency.coroutine.resource.Resource

suspend fun <Data : Any> executeForResult(executor: suspend () -> Data): Resource<Data> {
    return try {
        Resource.Success(executor.invoke())
    } catch (error: Throwable) {
        Resource.Error(error)
    }
}

suspend fun execute(executor: suspend () -> Unit): CompletableResource {
    return try {
        executor.invoke()
        CompletableResource.Completed
    } catch (error: Throwable) {
        CompletableResource.Failed(error)
    }
}