package io.github.t2penbix99wcoxkv3a4g.tonsign

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortOut
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.debug
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManager
import java.net.InetSocketAddress

object OSCSender {
    private const val PORT = 9000
    private const val IP = "127.0.0.1"
    private const val PARAM_PREFIX = "/avatar/parameters/"

    @Suppress("SpellCheckingInspection")
    private const val PARAM_CHAT_BOX = "/chatbox/input"

    private val oscClient = OSCPortOut(InetSocketAddress(IP, PORT))

    fun send(bool: Boolean) = sendParam(ConfigManager.config.oscParamTonSign, bool)
    fun sendTabun(bool: Boolean) = sendParam(ConfigManager.config.oscParamTonSignTabun, bool)
    fun sendOn(bool: Boolean) = sendParam(ConfigManager.config.oscParamTonSignOn, bool)

    @Suppress("MemberVisibilityCanBePrivate")
    fun <T> sendParam(param: String, input: T) = send("$PARAM_PREFIX$param", input)

    @Suppress("unused")
    fun sendChat(msg: String, direct: Boolean = true, complete: Boolean = false) {
        val message = OSCMessage(PARAM_CHAT_BOX, mutableListOf(msg, direct, complete))
        oscClient.send(message)
        Logger.debug<OSCSender> { "$PARAM_CHAT_BOX $msg $direct $complete" }
    }

    @Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")
    fun <T> send(addres: String, input: T) {
        val message = OSCMessage(addres, mutableListOf(input))
        oscClient.send(message)
        Logger.debug<OSCSender> { "$addres $input" }
    }
}