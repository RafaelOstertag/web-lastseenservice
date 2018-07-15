package ch.guengel.webtools

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.Test
import java.sql.Connection
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

object DatabaseConnection {
    val db by lazy {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    }
}

class DaoTest {
    private fun test(testFun: () -> Unit) {
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

    @Test
    fun `simple client insert`() {
        test {

            Client.new {
                ip = "127.0.0.1"
            }

            val allClient = Client.all()
            assertEquals(1, allClient.count())
        }
    }

    @Test
    fun `client Uniqueness`() {
        assertFailsWith<ExposedSQLException> {
            test {
                Client.new {
                    ip = "127.0.0.1"
                }

                Client.new {
                    ip = "127.0.0.1"
                }
            }
        }
    }

    @Test
    fun `seen reference`() {
        test {
            val client = Client.new {
                ip = "127.0.0.1"
            }

            val seen = Seen.new {
                seenOn = DateTime()
            }

            seen.client = client

            val clients = Client.find { Clients.ip eq "127.0.0.1" }
            assertEquals(1, clients.count())

            val actual = clients.first()
            assertEquals(1, actual.seens.count())


        }
    }
}

