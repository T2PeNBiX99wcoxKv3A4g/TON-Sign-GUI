package io.github.t2penbix99wcoxkv3a4g.tonsign.api

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.ApiScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.error
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
import okhttp3.*
import java.io.IOException

class ApiClient {
    private companion object {
        private const val BASE_URL = "https://vrchat.com/api/1"
        private const val BASE_URL_WITHOUT_HTTP = "vrchat.com/api/1"
        private const val BASE_URL_HOST = "vrchat.com"
        private val userAgent = "${Utils.ID}/${Utils.version}"
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiInterceptor { clientApiKey })
        .cookieJar(ApiCookieHelper.cookieJar)
        .build()

    private val apiClient = Configuration.getDefaultApiClient()
    private val authApi = AuthenticationApi(apiClient)
    private val authCookie = apiClient.getAuthentication("authCookie") as ApiKeyAuth
    private val authHeader = apiClient.getAuthentication("authHeader") as HttpBasicAuth
    private val twoFactorAuthCookie = apiClient.getAuthentication("twoFactorAuthCookie") as ApiKeyAuth
    private val apiInstance = SystemApi(apiClient)

    private var _config: JsonObject? = null

    val config: JsonObject?
        get() {
            if (_config == null)
                getConfig {
                    _config = it
                    authCookie.apiKey = clientApiKey
                }
            return _config
        }

    private val clientApiKey: String?
        get() = config?.get("clientApiKey")?.jsonPrimitive?.content

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
            is ApiException -> Logger.error<ApiClient>(error) { "Status code: ${error.code}\nReason: ${error.responseBody}\nResponse headers: ${error.responseHeaders}" }

            else -> Logger.error<ApiClient>(error) { "Error: ${error.localizedMessage}" }
        }
    }

    private fun tryRequest(
        request: Request,
        retryTime: Int = 10,
        retryDelay: Long = 5000L,
        onResponse: (Response) -> Unit
    ) {
        ApiScope.launch {
            var needRetry = true
            var nowRetryTime = 0

            while (true) {
                if (nowRetryTime >= retryTime || !needRetry)
                    break

                runCatching {
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Logger.error<ApiClient>(e) { "Request Error: ${e.localizedMessage}" }
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

    private fun getConfig(onGet: (JsonObject) -> Unit) {
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
        ApiScope.launch {
            val credential = Credentials.basic(username, password)
            val url = "$BASE_URL/auth/user"
//            ApiCookieHelper.setCookie(url, "auth", clientApiKey!!)
            val request = Request.Builder()
                .url(url)
                .header("Authorization", credential) // TODO: Maybe base64 is wrong?
                .header("Content-Type", "application/json")
                .get()
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
