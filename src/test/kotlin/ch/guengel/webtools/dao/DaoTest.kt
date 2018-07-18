package ch.guengel.webtools.dao

import ch.guengel.webtools.databaseTest
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.joda.time.DateTime
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DaoTest {
    @Test
    fun `simple client insert`() {
        databaseTest {

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
            databaseTest {
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
        databaseTest {
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

