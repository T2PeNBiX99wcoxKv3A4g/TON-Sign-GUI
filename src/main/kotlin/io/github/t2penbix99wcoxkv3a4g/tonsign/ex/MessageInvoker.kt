package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

fun (() -> Any?).toStringSafe(handle: (exception: Throwable) -> String) =
    runCatching { invoke().toString() }.getOrElse(handle)

fun ((Throwable?) -> Any?).toStringSafe(throwable: Throwable?, handle: (exception: Throwable) -> String) =
    runCatching { invoke(throwable).toString() }.getOrElse(handle)

fun (() -> Any?).toStringSafe() =
    toStringSafe { "Log message invocation failed: ${it.localizedMessage}\n${it.stackTrace}" }

fun ((Throwable?) -> Any?).toStringSafe(throwable: Throwable?) =
    toStringSafe(throwable) { "Log message invocation failed: ${it.localizedMessage}\n${it.stackTrace}" }