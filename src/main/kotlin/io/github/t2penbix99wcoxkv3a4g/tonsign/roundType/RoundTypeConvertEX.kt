package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

fun String.jpToEn() = RoundTypeConvert.jpToEn(this)
fun String.getTypeOfRound() = RoundTypeConvert.getTypeOfRound(this)

fun RoundType.getTextOfRound() = RoundTypeConvert.getTextOfRound(this)
fun RoundType.classifyRound() = RoundTypeConvert.classifyRound(this)
fun RoundType.isSpecialOrClassic() = RoundTypeConvert.isSpecialOrClassic(this)