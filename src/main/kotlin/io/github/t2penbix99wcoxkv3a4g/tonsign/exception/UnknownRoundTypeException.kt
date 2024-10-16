package io.github.t2penbix99wcoxkv3a4g.tonsign.exception

@Suppress("unused")
class UnknownRoundTypeException: Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}