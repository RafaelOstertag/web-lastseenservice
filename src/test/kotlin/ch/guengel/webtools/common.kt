package ch.guengel.webtools

import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seens
import org.jetbrains.exposed.dao.flushCache
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

internal object testDatabaseConnection {
    val db by lazy {
        val databaseConnection = DatabaseConnection("jdbc:h2:mem:databaseTest;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE")
        databaseConnection.database
    }
}

internal fun databaseTest(testFun: () -> Unit) {
    transaction(
        transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ,
        db = testDatabaseConnection.db,
        repetitionAttempts = 0
    ) {
        Seens.deleteAll()
        Clients.deleteAll()
        testFun()
        flushCache()
    }
}


