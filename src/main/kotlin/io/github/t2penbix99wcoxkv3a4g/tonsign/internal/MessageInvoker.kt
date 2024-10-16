package io.github.t2penbix99wcoxkv3a4g.tonsign.internal

internal fun (() -> Any?).toStringSafe(): String {
    return try {
        invoke().toString()
    } catch (e: Exception) {
        return "Log message invocation failed: $e"
    }
}

//internal expect object ErrorMessageProducer {
//    fun getErrorLog(e: Exception): String
//}
//
//internal object DefaultErrorMessageProducer {
//    fun getErrorLog(e: Exception): String = "Log message invocation failed: $e"
//}