package io.github.t2penbix99wcoxkv3a4g.tonsign.api

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.ApiScope
import io.github.vrchatapi.Configuration
import io.github.vrchatapi.api.AuthenticationApi
import io.github.vrchatapi.auth.HttpBasicAuth
import kotlinx.coroutines.launch

class ApiClient() {
    val apiClient = Configuration.getDefaultApiClient()
    val authApi = AuthenticationApi(apiClient)
    val authHeader = apiClient.getAuthentication("authHeader") as HttpBasicAuth
    val scope = ApiScope()
    
    init {
        apiClient.setUserAgent("${Utils.ID}/${Utils.version}")
        Utils.logger.debug { "Utils.version: ${Utils.version}" }
    }

    fun login(username: String, password: String) {
        scope.launch{
            authHeader.username = username
            authHeader.password = password

            val result = authApi.currentUser
            Utils.logger.debug { "Result: $result" }
        }
    }
}