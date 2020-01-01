package ch.guengel.webtools.application

import ch.guengel.webtools.DatabaseConnection
import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seens
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

internal class DatabaseConnectionTest {

    @Test
    fun `initialization test`() {
        val databaseConnection = DatabaseConnection("jdbc:h2:mem:databaseTest")
        transaction(db = databaseConnection.database) {
            create(Seens, Clients)
        }
    }
}