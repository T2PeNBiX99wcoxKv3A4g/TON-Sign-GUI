package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

fun RoundFlags.removeSafe(flag: RoundFlag) {
    if (!contains(flag)) return
    remove(flag)
}

fun RoundFlags.addSafe(flag: RoundFlag) {
    if (contains(flag)) return
    add(flag)
}