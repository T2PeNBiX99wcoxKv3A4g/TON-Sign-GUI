package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.LongEnumColumnAdapter
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.RoundData
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.Save
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.WonOrLost
import kotlin.io.path.Path

private val filePath = Path(Utils.currentWorkingDirectory, "test.db")

fun main() {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:test.db")
    val file = filePath.toFile()

//    Save.Schema.migrate(driver, 0, Save.Schema.version, AfterVersion(1) {
//        it.
//    })

    if (!file.exists())
        Save.Schema.create(driver)

    val database = Save(
        driver,
        roundDataAdapter = RoundData.Adapter(
            roundTypeAdapter = LongEnumColumnAdapter(),
            isWonAdapter = LongEnumColumnAdapter()
        )
    )

    val queries = database.saveQueries

    queries.selectAll { time, roundType, map, mapId, roundTime, playerTime, isDeath, isWon ->
        time
    }.executeAsList()

    queries.insert(
        RoundData(
            1324,
            RoundType.Classic,
            "Test",
            112,
            1231231,
            12313213,
            false,
            WonOrLost.Won
        )
    )

    queries.selectAll().executeAsList().forEach {
        Utils.logger.info { "RoundData: $it" }
    }
}