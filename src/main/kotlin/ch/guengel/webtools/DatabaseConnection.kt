package ch.guengel.webtools

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class DatabaseConnection(
    jdbcUrl: String,
    userName: String = "",
    password: String = "",
    poolSize: Int = 10
) {
    private val dataSource: HikariDataSource
    val database: Database

    init {
        val config = createConfig(jdbcUrl, userName, password, poolSize)
        dataSource = runBlocking {
            val dataSource = connect(config)
            setupDatabase(dataSource)
            dataSource
        }

        database = Database.connect(dataSource)
        logger.info("Connected to $jdbcUrl as $userName")
    }

    private fun createConfig(jdbcUrl: String, userName: String, password: String, poolSize: Int): HikariConfig {
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = userName
        config.password = password
        config.isAutoCommit = false
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        config.maximumPoolSize = poolSize
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

    private fun setupDatabase(dataSource: DataSource) {
        val connection = dataSource.connection
        val database =
            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))
        val liquibase = Liquibase("/db/changelog/db.changelog-master.yaml", ClassLoaderResourceAccessor(), database)
        liquibase.update(Contexts(), LabelExpression())
    }

    companion object {
        val logger = LoggerFactory.getLogger(DatabaseConnection::class.java)
    }
}