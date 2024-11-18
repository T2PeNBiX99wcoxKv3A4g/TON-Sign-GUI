package io.github.t2penbix99wcoxkv3a4g.tonsign.test

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerData
import io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model.PlayerStatus
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
            mapIdAdapter = IntColumnAdapter,
            playersAdapter = PlayerDataListColumnAdapter,
            terrorsAdapter = ArrayIntListColumnAdapter,
            isWonAdapter = LongEnumColumnAdapter(),
        )
    )

    val queries = database.saveQueries

    queries.insert(
        RoundData(
            1324,
            RoundType.Classic,
            "Test",
            112,
            1231231,
            12313213,
            mutableListOf(PlayerData("Test", id = null, PlayerStatus.Unknown, null)),
            arrayListOf(1, 2, 3),
            false,
            WonOrLost.Won
        )
    )

    queries.insert(
        RoundData(
            1325,
            RoundType.Custom,
            "Test2",
            113,
            1231231,
            12313213,
            mutableListOf(
                PlayerData("Test", id = null, PlayerStatus.Unknown, null),
                PlayerData("Test2", id = null, PlayerStatus.Unknown, null)
            ),
            arrayListOf(1, 2, 3),
            false,
            WonOrLost.Won
        )
    )

    queries.insert(
        RoundData(
            1325,
            RoundType.Custom,
            "Test2",
            113,
            1231231,
            12313213,
            mutableListOf(
                PlayerData("Test", id = null, PlayerStatus.Unknown, null),
                PlayerData("Test2", id = null, PlayerStatus.Unknown, null),
                PlayerData("Test3", id = null, PlayerStatus.Unknown, null)
            ),
            arrayListOf(1, 2, 3),
            false,
            WonOrLost.Won
        )
    )

    queries.selectAll().executeAsList().forEach {
        Utils.logger.info { "RoundData: $it" }
    }

    val data = queries.selectOfTime(1325).executeAsOneOrNull()

    Utils.logger.info { "RoundData Found: $data" }

    val test = queries.selectLast().executeAsOneOrNull()

    Utils.logger.info { "RoundData Found 2: ${test?.MAX}" }

    if (test == null) return

    queries.selectOfTime(test.MAX!!).executeAsList().forEach {
        Utils.logger.info { "RoundData Found 3: $it" }
    }
}