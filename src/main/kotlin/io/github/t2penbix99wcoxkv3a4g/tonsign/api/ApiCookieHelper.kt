/*  Cookie Handling Lib for OkHttp3
 *
 *  Copyright (c) 2017 Tom Misawa, riversun.org@gmail.com
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 *  
 */
package io.github.t2penbix99wcoxkv3a4g.tonsign.api

import io.github.t2penbix99wcoxkv3a4g.tonsign.event.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.CookieData
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.toCookie
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.toData
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.concurrent.ConcurrentHashMap

// https://github.com/riversun/okhttp3-cookie-helper/blob/master/src/main/java/org/riversun/okhttp3/OkHttp3CookieHelper.java
object ApiCookieHelper {
    private val _serverCookieStore = ConcurrentHashMap<String, ArrayList<Cookie>>()
    private val _clientCookieStore = ConcurrentHashMap<String, ArrayList<Cookie>>()

    init {
        EventBus.register(this)
    }

    val cookieJar: CookieJar
        get() = _cookieJar

    private val _cookieJar = object : CookieJar {
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            var serverCookieList = _serverCookieStore[url.host]

            if (serverCookieList == null)
                serverCookieList = arrayListOf()

            val clientCookieStore = _clientCookieStore[url.host]

            if (clientCookieStore != null)
                serverCookieList.addAll(clientCookieStore)

            return serverCookieList
        }

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            _serverCookieStore[url.host] = ArrayList(cookies)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setCookie(url: String, cookie: Cookie) {
        val host = url.toHttpUrlOrNull()?.host
        requireNotNull(host)

        var cookieListForUrl = _clientCookieStore[host]
        if (cookieListForUrl == null) {
            cookieListForUrl = arrayListOf()
            _clientCookieStore[host] = cookieListForUrl
        }
        putCookie(cookieListForUrl, cookie)
    }

    fun setCookie(url: String, cookieName: String, cookieValue: String) {
        val httpUrl = url.toHttpUrlOrNull()
        requireNotNull(httpUrl)
        val cookie = Cookie.parse(httpUrl, "$cookieName=$cookieValue")
        requireNotNull(cookie)
        setCookie(url, cookie)
    }

    fun setCookie(httpUrl: HttpUrl, cookieName: String, cookieValue: String) {
        val cookie = Cookie.parse(httpUrl, "$cookieName=$cookieValue")
        requireNotNull(cookie)
        setCookie(httpUrl.host, cookie)
    }

    private fun putCookie(storedCookieList: ArrayList<Cookie>, newCookie: Cookie) {
        val oldCookie = storedCookieList.firstOrNull { it.name + it.path == newCookie.name + newCookie.path }

        if (oldCookie != null)
            storedCookieList.remove(oldCookie)
        storedCookieList.add(newCookie)
    }

    private fun saveStore(map: ConcurrentHashMap<String, ArrayList<Cookie>>): HashMap<String, ArrayList<CookieData>> {
        val retMap: HashMap<String, ArrayList<CookieData>> = hashMapOf()
        map.forEach {
            val cookieList = it.value
            val cookieDataList = arrayListOf<CookieData>()

            cookieList.forEach { cookie ->
                cookieDataList.add(cookie.toData())
            }

            retMap[it.key] = cookieDataList
        }
        return retMap
    }

    private fun loadStore(
        map: ConcurrentHashMap<String, ArrayList<Cookie>>,
        dataMap: HashMap<String, ArrayList<CookieData>>
    ) {
        dataMap.forEach {
            val cookieDataList = it.value
            val cookieList = arrayListOf<Cookie>()

            cookieDataList.forEach { cookieData ->
                cookieList.add(cookieData.toCookie())
            }

            map[it.key] = cookieList
        }
    }

    private fun clear() {
        _serverCookieStore.clear()
        _clientCookieStore.clear()
    }

    private val serverCookieStore: HashMap<String, ArrayList<CookieData>>
        get() = saveStore(_serverCookieStore)

    private val clientCookieStore: HashMap<String, ArrayList<CookieData>>
        get() = saveStore(_clientCookieStore)

    private fun load(
        serverCookieStore: HashMap<String, ArrayList<CookieData>>,
        clientCookieStore: HashMap<String, ArrayList<CookieData>>
    ) {
        clear()
        loadStore(_serverCookieStore, serverCookieStore)
        loadStore(_clientCookieStore, clientCookieStore)
    }

    @Subscribe(Events.OnSecretsLoaded)
    private fun onLoaded(event: OnSecretsLoadedEvent) {
        val secrets = event.secrets
        load(secrets.serverCookieStore, secrets.clientCookieStore)
    }

    @Subscribe(Events.OnSecretsStartSave)
    private fun onStartSave(event: OnSecretsStartSaveEvent) {
        val secrets = event.secrets
        secrets.serverCookieStore.clear()
        secrets.serverCookieStore.putAll(serverCookieStore)
        secrets.clientCookieStore.clear()
        secrets.clientCookieStore.putAll(clientCookieStore)
    }
}