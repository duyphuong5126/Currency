package com.phuongduy.currency.data

import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.isNetworkError(): Boolean {
    return this is ConnectException || this is UnknownHostException || this is NoRouteToHostException
}

fun Throwable.isTimeoutException(): Boolean {
    return this is SocketTimeoutException
}