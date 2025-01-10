package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

import java.util.*

enum class RoundFlag {
    WaitingFirstSpecial,
    NotSure,
    MysticMoonFinish,
    BloodMoonFinish,
    TwilightFinish,
    SolsticeFinish
}

typealias RoundFlags = EnumSet<RoundFlag>