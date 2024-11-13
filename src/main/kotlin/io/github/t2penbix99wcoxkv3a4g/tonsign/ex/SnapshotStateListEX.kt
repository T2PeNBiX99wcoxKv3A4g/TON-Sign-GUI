package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import androidx.compose.runtime.snapshots.SnapshotStateList

fun <T> SnapshotStateList<T>.swapList(newList: Collection<T>) {
    clear()
    addAll(newList)
}