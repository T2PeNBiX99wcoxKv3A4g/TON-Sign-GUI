package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

fun RoundFlags.removeSafe(flag: RoundFlag) {
    if (!contains(flag)) return
    remove(flag)
}

fun RoundFlags.addSafe(flag: RoundFlag) {
    if (contains(flag)) return
    add(flag)
}

// https://stackoverflow.com/questions/2199399/storing-enumset-in-a-database
fun RoundFlags.encode(): Int {
    var ret = 0

    this.forEach {
        ret = ret or (1 shl it.ordinal) // ret |= (1 << it.ordinal) in Java
    }

    return ret
}