package io.github.jakubgrotha.secretkeeper

import java.lang.Exception

fun <T> Sequence<T>.firstOrThrow(exception: Exception = RuntimeException()): T = this.firstOrNull() ?: throw exception
