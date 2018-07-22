package ch.guengel.webtools

import ch.guengel.webtools.application.DatabaseConnection
import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seens
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

internal object testDatabaseConnection {
    val db by lazy {
        val databaseConnection = DatabaseConnection("jdbc:h2:mem:databaseTest")
        databaseConnection.database
    }
}

internal fun databaseTest(testFun: () -> Unit) {
    transaction(
        transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ,
        db = testDatabaseConnection.db,
        repetitionAttempts = 0
    ) {
        create(Seens, Clients)
        testFun()
        flushCache()
        TransactionManager.current().rollback()
    }
}


