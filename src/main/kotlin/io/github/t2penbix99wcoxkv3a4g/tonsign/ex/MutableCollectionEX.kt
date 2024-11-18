package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

fun <T> MutableCollection<T>.swapList(newList: Collection<T>) {
    clear()
    addAll(newList)
}

fun <T> MutableCollection<T>.swapList(newList: Map<*, T>) {
    clear()
    newList.forEach { add(it.value) }
}