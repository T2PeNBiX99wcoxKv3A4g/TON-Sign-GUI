package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import kotlinx.serialization.Serializable

@Serializable
internal data class Secrets(
    val username: String?,
    val password: String?,
    val serverCookieStore: HashMap<String, ArrayList<CookieData>>,
    val clientCookieStore: HashMap<String, ArrayList<CookieData>>
)