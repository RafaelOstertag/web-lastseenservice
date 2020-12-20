package ch.guengel.webtools.application

import ch.guengel.webtools.DatabaseConnection
import org.junit.jupiter.api.Test

internal class DatabaseConnectionTest {

    @Test
    fun `initialization test`() {
        DatabaseConnection("jdbc:h2:mem:databaseTest")
    }
}