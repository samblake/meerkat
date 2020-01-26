package com.github.samblake.meerkat.edge

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Database {

    fun init() = with (Configuration) {
        Database.connect(hikari(jdbcDriver(), jdbcUrl(), jdbcUsername(), jdbcPassword()))
    }

    private fun hikari(driverClassName: String, jdbcUrl: String, username: String, password: String): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = driverClassName
        config.jdbcUrl = jdbcUrl
        config.username = username
        config.password = password
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> query(block: suspend () -> T): T = newSuspendedTransaction { block() }

}
