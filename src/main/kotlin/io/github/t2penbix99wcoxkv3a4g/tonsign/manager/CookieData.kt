package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import kotlinx.serialization.Serializable

@Serializable
internal data class CookieData(
    val domain: String,
    val expiresAt: Long,
    val hostOnly: Boolean,
    val httpOnly: Boolean,
    val name: String,
    val path: String,
    val persistent: Boolean,
    val secure: Boolean,
    val value: String
)