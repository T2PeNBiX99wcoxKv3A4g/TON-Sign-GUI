package io.github.t2penbix99wcoxkv3a4g.tonsign

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortOut
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.debug
import java.net.InetSocketAddress

@Suppress("unused")
object OSCSender {
    private const val PORT = 9000
    private const val IP = "127.0.0.1"
    private const val PARAM_TON_SIGN = "/avatar/parameters/TON_Sign"
    private const val PARAM_TON_SIGN_TABUN = "/avatar/parameters/TON_Sign_Tabun"

    @Suppress("SpellCheckingInspection")
    private const val PARAM_CHAT_BOX = "/chatbox/input"

    private val oscClient = OSCPortOut(InetSocketAddress(IP, PORT))

    fun send(bool: Boolean) = send(PARAM_TON_SIGN, bool)
    fun sendTabun(bool: Boolean) = send(PARAM_TON_SIGN_TABUN, bool)

    fun <T> sendParam(param: String, input: T) = send("/avatar/parameters/$param", input)

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