package ch.guengel.webtools

import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seens
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class DatabaseConnection(
        jdbcUrl: String,
        userName: String = "",
        password: String = ""
) {
    private val dataSource: HikariDataSource
    val database: Database

    init {
        val config = createConfig(jdbcUrl, userName, password)
        dataSource = runBlocking {
            connect(config)
        }

        database = Database.connect(dataSource)
        transaction(database) {
            create(Seens, Clients)
        }
        logger.info("Connected to $jdbcUrl as $userName")
    }

    private fun createConfig(jdbcUrl: String, userName: String, password: String): HikariConfig {
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = userName
        config.password = password
        config.isAutoCommit = false
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        return config
    }

    private suspend fun connect(config: HikariConfig): HikariDataSource {
        var sleep = 1000L
        var retry = 0
        while (true) {
            try {
                logger.info("Trying to connect to database")
                val datasource = HikariDataSource(config)
                logger.info("Successfully connected to database")
                return datasource
            } catch (e: Exception) {
                retry++
                logger.warn("Unable to connect to database. Retry $retry in ${sleep}ms")
                delay(sleep)
                sleep *= 2
            }
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(DatabaseConnection::class.java)
    }
}