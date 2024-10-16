package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import io.github.t2penbix99wcoxkv3a4g.tonsign.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager

fun main() {
    val test = "Test %s".format("test2")

    Utils.logger.info { test }
    Utils.logger.info { "Test: ${LanguageManager.get("log.next_round_should_be", "Test", "test2")}" }
    Logger.info("log.next_round_should_be", "Test", "test2")
}