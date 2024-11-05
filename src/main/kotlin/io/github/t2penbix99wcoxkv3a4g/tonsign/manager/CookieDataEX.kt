package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import okhttp3.Cookie

internal fun Cookie.toData(): CookieData {
    return CookieData(
        domain = domain,
        expiresAt = expiresAt,
        hostOnly = hostOnly,
        httpOnly = httpOnly,
        name = name,
        path = path,
        persistent = persistent,
        secure = secure,
        value = value
    )
}

internal fun CookieData.toCookie(): Cookie {
    val builder = Cookie.Builder()
    
    if (hostOnly)
        builder.hostOnlyDomain(domain)
    else
        builder.domain(domain)
    
    if (persistent)
        builder.expiresAt(expiresAt)

    if (httpOnly)
        builder.httpOnly()
    
    if (secure)
        builder.secure()
    
    builder.name(name)
        .path(path)
        .value(value)
    
    return builder.build()
}