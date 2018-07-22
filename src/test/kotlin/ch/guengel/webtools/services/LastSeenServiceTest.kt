package ch.guengel.webtools.services

import ch.guengel.webtools.dao.Client
import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seen
import ch.guengel.webtools.dao.Seens
import ch.guengel.webtools.testDatabaseConnection
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.sql.Connection


const val timeBase = 100_000L
const val timeIncrement = 10_000L


class LastSeenServiceTest {
    private val lastSeenService = LastSeenService(testDatabaseConnection.db)

    @Before
    fun setUp() {
        transaction(
            transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ,
            db = testDatabaseConnection.db,
            repetitionAttempts = 0
        ) {
            drop(Seens, Clients)
            create(Seens, Clients)
        }
    }

    @Test
    fun `get all clients`() {
        runBlocking {
            addFixture()
            val all = lastSeenService.getAll()

            assertEquals(all.size, 1)

            val (ip, from, to, timesSeen) = all[0]
            assertEquals("127.0.0.1", ip)
            assertEquals(DateTime(timeBase).toDateTimeISO().toString(), from)
            assertEquals(
                DateTime(timeBase + 2 * timeIncrement).toDateTimeISO().toString(),
                to
            )
            assertEquals(3, timesSeen)
        }
    }

    @Test
    fun `add first occurrence`() {
        runBlocking {
            val (ip, from, to, timesSeen) = lastSeenService.addIpNow("127.0.0.1")
            assertEquals("127.0.0.1", ip)
            assertEquals(to, from)
            assertEquals(1, timesSeen)

            transaction {
                val allClients = Client.all()
                assertEquals(1, allClients.count())
            }
        }
    }

    @Test
    fun `add subsequent occurrences`() {
        runBlocking {
            val addIpNow1 = lastSeenService.addIpNow("127.0.0.1")
            val addIpNow2 = lastSeenService.addIpNow("127.0.0.1")

            assertEquals(addIpNow1.from, addIpNow2.from)
            assertTrue(addIpNow1.to < addIpNow2.to)

            transaction {
                val allClients = Client.all()
                assertEquals(1, allClients.count())

                val client = allClients.elementAt(0)

                assertEquals(2, client.seens.count())
                assertEquals(client.ip, addIpNow1.ip)
            }
        }
    }

    private fun addFixture() {
        transaction {
            val client = Client.new {
                ip = "127.0.0.1"
            }

            val seen1 = Seen.new {
                seenOn = DateTime(timeBase)
            }
            seen1.client = client

            val seen2 = Seen.new {
                seenOn = DateTime(timeBase + timeIncrement)
            }
            seen2.client = client

            val seen3 = Seen.new {
                seenOn = DateTime(timeBase + 2 * timeIncrement)
            }
            seen3.client = client
        }
    }
}