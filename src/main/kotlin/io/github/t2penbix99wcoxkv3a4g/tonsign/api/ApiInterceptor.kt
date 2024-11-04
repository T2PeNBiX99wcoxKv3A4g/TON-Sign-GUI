package io.github.t2penbix99wcoxkv3a4g.tonsign.api

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import okhttp3.Interceptor
import okhttp3.Response

// https://stackoverflow.com/questions/26509107/how-to-specify-a-default-user-agent-for-okhttp-2-x-requests
class ApiInterceptor(val apiKey: () -> String?) : Interceptor {
    private companion object {
        private const val USER_AGENT = "User-Agent"
        private val userAgent = "${Utils.ID}/${Utils.version}"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        
        val newUrl = url.newBuilder()
            .addQueryParameter("apiKey", apiKey())
            .build()

        val newRequest = request.newBuilder()
            .url(newUrl)
            .header(USER_AGENT, userAgent)
            .build()

        return chain.proceed(newRequest)
    }
}