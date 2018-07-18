package ch.guengel.webtools.application

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

class DatabaseConnection(
    private val jdbcUrl: String,
    private val userName: String = "",
    private val password: String = ""
) {
    val database: Database

    init {
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = userName
        config.password = password
        config.isAutoCommit = false
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        val dataSource = HikariDataSource(config)
        database = Database.connect(dataSource)
    }
}