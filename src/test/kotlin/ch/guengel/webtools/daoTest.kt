package ch.guengel.webtools

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import kotlin.test.assertEquals

class DaoTest {
    val db by lazy {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    }

    @Test
    fun testClient(): Unit {
        db
        transaction {
            create(Clients)

            val client1 = Client.new {
                ip = "127.0.0.1"
            }

            val allClient = Client.all()
            assertEquals(1, allClient.count())
        }
    }
}

