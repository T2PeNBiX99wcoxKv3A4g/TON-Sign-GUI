package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.ConfigManage

fun main() {
    Utils.logger.info { "Test ${ConfigManage.config.language}" }
}