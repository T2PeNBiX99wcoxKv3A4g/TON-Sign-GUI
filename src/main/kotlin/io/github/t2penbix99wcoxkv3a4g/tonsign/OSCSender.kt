package io.github.t2penbix99wcoxkv3a4g.tonsign

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortOut
import java.net.InetSocketAddress

object OSCSender {
    private const val PORT = 9000
    private const val IP = "127.0.0.1"

    val oscClient = OSCPortOut(InetSocketAddress(IP, PORT))

    fun send(bool: Boolean) {
        send("/avatar/parameters/TON_Sign", bool)
    }

    fun <T> send(addres: String, input: T) {
        val message = OSCMessage(addres, mutableListOf(input))
        oscClient.send(message)
        Logger.debug("${OSCSender::class.simpleName} $addres $input")
    }
}