package com.phuongduy.currency

import org.mockito.Mockito.*

fun <T : Any> safelyArgThat(default: T, matcher: (T) -> Boolean): T {
    return argThat { it?.let(matcher) ?: false } ?: default
}

fun <T : Any> listThat(matcher: (List<T>) -> Boolean): List<T> {
    return safelyArgThat(emptyList(), matcher)
}

fun <T : Any> safelyAnyList(): List<T> {
    return anyList() ?: emptyList()
}