package ch.guengel.webtools

import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

const val timeBase = 100_000L
const val timeIncrement = 10_000L


class LastSeenServiceTest {
    val lastSeenService = LastSeenService(DatabaseConnection.db)

    @Test
    fun `get all clients`() {
        databaseTest {
            addFixture()

            val all = lastSeenService.getAll()

            assertEquals(all.size, 1)

            val (ip, from, to, timesSeen) = all.get(0)
            assertEquals("127.0.0.1", ip)
            assertEquals(DateTime(timeBase), from)
            assertEquals(DateTime(timeBase + 2 * timeIncrement), to)
            assertEquals(3, timesSeen)
        }
    }

    @Test
    fun `add first occurrence`() {
        databaseTest {
            val (ip, from, to, timesSeen) = lastSeenService.addIpNow("127.0.0.1")
            assertEquals("127.0.0.1", ip)
            assertEquals(to, from)
            assertEquals(1, timesSeen)

            val allClients = Client.all()
            assertEquals(1, allClients.count())
        }
    }

    @Test
    fun `add subsequent occurrences`() {
        databaseTest {
            val addIpNow1 = lastSeenService.addIpNow("127.0.0.1")
            val addIpNow2 = lastSeenService.addIpNow("127.0.0.1")

            assertEquals(addIpNow1.from, addIpNow2.from)
            assertTrue(addIpNow1.to < addIpNow2.to)

            val allClients = Client.all()
            assertEquals(1, allClients.count())

            val client = allClients.elementAt(0)

            assertEquals(2, client.seens.count())
            assertEquals(client.ip, addIpNow1.ip)
        }
    }

    private fun addFixture() {
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