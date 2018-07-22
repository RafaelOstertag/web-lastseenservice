package ch.guengel.webtools.application

import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seens
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnection(
    jdbcUrl: String,
    userName: String = "",
    password: String = ""
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
        transaction(database) {
            create(Seens, Clients)
        }
    }


}