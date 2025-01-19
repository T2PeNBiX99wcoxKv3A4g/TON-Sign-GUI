package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import java.util.*

enum class RoundFlag {
    WaitingFirstSpecial,
    NotSure,
    MysticMoonFinish,
    BloodMoonFinish,
    TwilightFinish,
    SolsticeFinish,
    Winter;

    companion object {
        // if change ordinal of enum will blow up
        fun decode(inputCode: Int): RoundFlags = runCatching {
            var code = inputCode
            val type = RoundFlag::class.java
            val values = type.enumConstants
            val result = EnumSet.noneOf(type)

            while (code != 0) {
                val ordinal = code.countTrailingZeroBits()
                code = code xor code.takeLowestOneBit() // code ^= Integer.lowestOneBit(code) in Java
                result.add(values[ordinal])
            }

            result
        }.getOrElse {
            Logger.error(it, { "exception.something_is_not_right" }, it.localizedMessage, it.stackTraceToString())
            EnumSet.noneOf(RoundFlag::class.java)
        }
    }
}


typealias RoundFlags = EnumSet<RoundFlag>