package ch.guengel.webtools

import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seens
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

internal object DatabaseConnection {
    val db by lazy {
        Database.connect("jdbc:h2:mem:databaseTest", driver = "org.h2.Driver")
    }
}

internal fun databaseTest(testFun: () -> Unit) {
    transaction(
        transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ,
        db = DatabaseConnection.db,
        repetitionAttempts = 0
    ) {
        create(Seens, Clients)
        testFun()
        flushCache()
        TransactionManager.current().rollback()
    }
}


