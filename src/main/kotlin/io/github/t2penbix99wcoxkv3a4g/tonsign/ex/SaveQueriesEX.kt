package io.github.t2penbix99wcoxkv3a4g.tonsign.ex

import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.SaveDataUpdateScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.SaveQueries
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import kotlinx.coroutines.runBlocking

fun SaveQueries.last() = runBlocking(SaveDataUpdateScope.coroutineContext) {
    selectLast().executeAsOneOrNull()?.MAX
}

fun SaveQueries.timeOf(time: Long) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    selectOfTime(time).executeAsOneOrNull()
}

fun SaveQueries.setIsWon(time: Long, isWon: WonOrLost) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    updateIsWon(isWon, time)
}

fun SaveQueries.setRoundTime(time: Long, roundTime: Long) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    updateRoundTime(roundTime, time)
}

fun SaveQueries.setPlayerTime(time: Long, playerTime: Long) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    updatePlayerTime(playerTime, time)
}

fun SaveQueries.add(roundData: RoundData) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    insert(roundData)
}

fun SaveQueries.setPlayers(time: Long, players: MutableList<PlayerData>) =
    runBlocking(SaveDataUpdateScope.coroutineContext) {
        updatePlayers(players, time)
    }

fun SaveQueries.setIsDeath(time: Long, isDeath: Boolean) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    updateIsDeath(isDeath, time)
}

fun SaveQueries.setTerrors(time: Long, terrors: ArrayList<Int>) = runBlocking(SaveDataUpdateScope.coroutineContext) {
    updateTerrors(terrors, time)
}

fun SaveQueries.getAll() = runBlocking(SaveDataUpdateScope.coroutineContext) {
    selectAll().executeAsList()
}