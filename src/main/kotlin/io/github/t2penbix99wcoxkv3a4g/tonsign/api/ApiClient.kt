package io.github.t2penbix99wcoxkv3a4g.tonsign.api

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.ApiScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.vrchatapi.ApiException
import io.github.vrchatapi.Configuration
import io.github.vrchatapi.api.AuthenticationApi
import io.github.vrchatapi.api.SystemApi
import io.github.vrchatapi.auth.ApiKeyAuth
import io.github.vrchatapi.auth.HttpBasicAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.CookieManager

class ApiClient() {
    private companion object {
        private const val BASE_URL = "https://api.vrchat.cloud/api/1"
        private const val BASE_URL_WITHOUT_HTTP = "api.vrchat.cloud/api/1"
        private val userAgent = "${Utils.ID}/${Utils.version}"
    }

    private val cookieHandler = CookieManager()
    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiInterceptor { clientApiKey })
        .build()

    private val apiClient = Configuration.getDefaultApiClient()
    private val authApi = AuthenticationApi(apiClient)
    private val authCookie = apiClient.getAuthentication("authCookie") as ApiKeyAuth
    private val authHeader = apiClient.getAuthentication("authHeader") as HttpBasicAuth
    private val twoFactorAuthCookie = apiClient.getAuthentication("twoFactorAuthCookie") as ApiKeyAuth
    private val apiInstance = SystemApi(apiClient)
    private val scope = ApiScope()

    private var _config: JsonObject? = null

    val config: JsonObject?
        get() = _config

    private val clientApiKey: String?
        get() = _config?.get("clientApiKey")?.jsonPrimitive?.content

    init {
        apiClient.setUserAgent(userAgent)
        apiClient.setBasePath(BASE_URL)

        getConfig {
            _config = it
            authCookie.apiKey = clientApiKey
        }
    }

    private fun exceptionHandle(error: Throwable) {
        when (error) {
            is ApiException -> Logger.error(
                error,
                "Status code: ${error.code}\nReason: ${error.responseBody}\nResponse headers: ${error.responseHeaders}"
            )

            else -> Logger.error({ this::class.simpleName!! }, error, "Error: ")
        }
    }

    private fun tryRequest(
        request: Request,
        retryTime: Int = 10,
        retryDelay: Long = 5000L,
        onResponse: (Response) -> Unit
    ) {
        scope.launch {
            var needRetry = true
            var nowRetryTime = 0

            while (true) {
                if (nowRetryTime >= retryTime || !needRetry)
                    break

                runCatching {
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Logger.error({ this::class.simpleName!! }, e, "Request Error: ")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            response.use {
                                if (!response.isSuccessful)
                                    return Utils.logger.error { "Message: ${it.message}\nStatus code: ${it.code}\nReason: ${it.body?.string()}\nResponse headers: ${it.headers}" }

                                needRetry = false
                                onResponse(response)
                            }
                        }
                    })
                }.getOrElse {
                    exceptionHandle(it)
                }

                nowRetryTime++

                if (needRetry)
                    delay(retryDelay)
            }
        }
    }

    fun getConfig(onGet: (JsonObject) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL/config")
            .build()

        tryRequest(request) {
            val body = it.body!!
            val json = Json.parseToJsonElement(body.string())

            Utils.logger.debug { "Config Json: $json" }

            onGet(json.jsonObject)
        }
    }

    fun login(username: String, password: String) {
        if (clientApiKey.isNullOrEmpty()) return
        scope.launch {
            val credential = Credentials.basic(username, password)

            val url = HttpUrl.Builder()
                .scheme("https")
                .host("$BASE_URL_WITHOUT_HTTP/auth/user")
                .username(username)
                .password(password)
                .addQueryParameter("apiKey",clientApiKey)
                .build()
            
            val request = Request.Builder()
                .url(url)
                .header("Authorization", credential)
                .build()

            tryRequest(request, 1) {
                val cookie = it.headers("Set-Cookie")

                if (cookie.isEmpty())
                    return@tryRequest

                cookie.forEach {
                    Utils.logger.debug { "cookie: $it" }
                }
            }
        }
    }
}
