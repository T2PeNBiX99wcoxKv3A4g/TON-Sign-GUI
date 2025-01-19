package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.*
import io.github.t2penbix99wcoxkv3a4g.tonsign.data.Save as SqlSave

// https://github.com/sqldelight/sqldelight/issues/1605
object SaveManager {
    private const val FILE_NAME = "save.db"
    private val driver: JdbcSqliteDriver by lazy { JdbcSqliteDriver("jdbc:sqlite:$FILE_NAME") }

    val database: SqlSave by lazy {
        val currentVer = version
        if (currentVer == 0L) {
            SqlSave.Schema.create(driver)
            version = SqlSave.Schema.version
            Utils.logger.debug { "init: created tables, setVersion to 1" }
        } else {
            val schemaVer = SqlSave.Schema.version
            if (schemaVer > currentVer) {
                SqlSave.Schema.migrate(driver, currentVer, schemaVer)
                version = schemaVer
                Utils.logger.debug { "init: migrated from $currentVer to $schemaVer" }
            } else {
                Utils.logger.debug { "init" }
            }
        }

        SqlSave(
            driver,
            roundDataAdapter = RoundData.Adapter(
                roundTypeAdapter = LongEnumColumnAdapter(),
                roundFlagsAdapter = RoundFlagsColumnAdapter,
                mapIdAdapter = IntColumnAdapter,
                playersAdapter = PlayerDataListColumnAdapter,
                terrorsAdapter = ArrayIntListColumnAdapter,
                isWonAdapter = LongEnumColumnAdapter(),
            )
        )
    }

    private var version: Long
        get() {
            val queryResult = driver.executeQuery(
                identifier = null,
                sql = "PRAGMA user_version;",
                mapper = { sqlCursor: SqlCursor -> QueryResult.Value(sqlCursor.getLong(0)) },
                parameters = 0,
                binders = null
            )
            return queryResult.value!!
        }
        set(value) {
            driver.execute(
                identifier = null,
                sql = "PRAGMA user_version = $value;",
                parameters = 0,
                binders = null
            )
        }

    fun close() {
        driver.close()
    }
}