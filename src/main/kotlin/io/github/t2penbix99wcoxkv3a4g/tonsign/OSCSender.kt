package io.github.t2penbix99wcoxkv3a4g.tonsign

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortOut
import java.net.InetSocketAddress

class OSCSender(ip: String, port: Int) {
    companion object {
        private const val PORT = 9000
        private const val IP = "127.0.0.1"
        private var _instance: OSCSender? = null

        val VRChat: OSCSender
            get() {
                if (_instance == null)
                    _instance = OSCSender(IP, PORT)
                return _instance!!
            }
    }

    val oscClient = OSCPortOut(InetSocketAddress(ip, port))

    fun send(bool: Boolean) {
        send("/avatar/parameters/TON_Sign", bool)
    }

    fun <T> send(addres: String, input: T) {
        val message = OSCMessage(addres, mutableListOf(input))
        oscClient.send(message)
        Utils.logger.debug { "$addres $input" }
    }
}