package ch.guengel.webtools.services

import ch.guengel.webtools.DateTimeAdjuster
import ch.guengel.webtools.dao.Client
import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seen
import ch.guengel.webtools.dto.Occurrences
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class LastSeenService(private val database: Database) {
    private val dispatcher = newFixedThreadPoolContext(5, "database-pool")

    suspend fun getAll(): List<Occurrences> {
        return withContext(dispatcher) {
            transaction(database) {
                Client.all()
                    .map {
                        with(it) {
                            val latest = latestSeen()
                            val earliest = earliestSeen()
                            Occurrences(
                                ip = ip,
                                timesSeen = seens.count(),
                                from = earliest.toString(),
                                to = latest.toString()
                            )
                        }
                    }
                    .sortedBy { it.ip }
            }
        }
    }

    suspend fun addIpNow(ip: String): Occurrences {
        return withContext(dispatcher) {
            transaction(database) {
                val clients = Client.find { Clients.ip eq ip }
                val client = clients.elementAtOrNull(0) ?: Client.new { this.ip = ip }

                val newSeen = Seen.new { seenOn = DateTime() }
                newSeen.client = client

                Occurrences(
                    ip = ip,
                    from = client.earliestSeen().toString(),
                    to = client.latestSeen().toString(),
                    timesSeen = client.seens.count()
                )
            }
        }
    }

    suspend fun countOccurrencesSince(ip: String, timeSpecification: String): Occurrences {
        val currentDateTime = DateTime()
        val dateTimeAdjuster = DateTimeAdjuster(currentDateTime)
        val from = dateTimeAdjuster.by(timeSpecification)

        return withContext(dispatcher) {
            transaction(database) {
                val client = Client.find { Clients.ip eq ip }.first()
                val occurrences = client.seens.fold(0) { acc, seen ->
                    if (seen.seenOn.isBefore(currentDateTime) && seen.seenOn.isAfter(from))
                        acc + 1
                    else
                        acc
                }

                Occurrences(
                    ip = ip,
                    from = from.toString(),
                    to = currentDateTime.toString(),
                    timesSeen = occurrences
                )
            }
        }
    }

    private fun Client.earliestSeen() = seens.fold(DateTime()) { acc, seen -> minOf(acc, seen.seenOn) }

    private fun Client.latestSeen() = seens.fold(DateTime(0L)) { acc, seen -> maxOf(acc, seen.seenOn) }
}

