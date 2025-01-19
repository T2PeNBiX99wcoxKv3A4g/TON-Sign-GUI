package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.SaveDataUpdateScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.SaveQueries
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundFlags
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

private val mutex = Mutex()

fun SaveQueries.last() = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        selectLast().executeAsOneOrNull()?.MAX
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.timeOf(time: Long) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        selectOfTime(time).executeAsOneOrNull()
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.setIsWon(time: Long, isWon: WonOrLost) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        updateIsWon(isWon, time)
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.setRoundTime(time: Long, roundTime: Long) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        updateRoundTime(roundTime, time)
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.setPlayerTime(time: Long, playerTime: Long) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        updatePlayerTime(playerTime, time)
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.add(roundData: RoundData) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        insert(roundData)
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.setPlayers(time: Long, players: MutableList<PlayerData>) =
    runBlocking(SaveDataUpdateScope.coroutineContext) {
        mutex.lock()
        try {
            updatePlayers(players, time)
        } finally {
            mutex.unlock()
        }
    }

fun SaveQueries.setIsDeath(time: Long, isDeath: Boolean) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        updateIsDeath(isDeath, time)
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.setTerrors(time: Long, terrors: ArrayList<Int>) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        updateTerrors(terrors, time)
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.setRoundFlags(time: Long, roundFlags: RoundFlags) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        updateRoundFlags(roundFlags, time)
    } finally {
        mutex.unlock()
    }
}

fun SaveQueries.getAll() = runBlocking(SaveDataUpdateScope.coroutineContext) {
    mutex.lock()
    try {
        selectAll().executeAsList()
    } finally {
        mutex.unlock()
    }
}