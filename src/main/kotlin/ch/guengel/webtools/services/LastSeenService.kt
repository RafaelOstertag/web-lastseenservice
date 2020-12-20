package ch.guengel.webtools.services

import ch.guengel.webtools.DateTimeAdjuster
import ch.guengel.webtools.dao.Client
import ch.guengel.webtools.dao.Clients
import ch.guengel.webtools.dao.Seen
import ch.guengel.webtools.dao.Seens
import ch.guengel.webtools.dto.Occurrences
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class LastSeenService(private val database: Database) {
    fun getAll(): List<Occurrences> = transaction(database) {
        Client.all()
            .map {
                with(it) {
                    val clientStats = clientStats(it.id.value).first()
                    Occurrences(
                        ip = ip,
                        from = clientStats[firstSeen]?.toString() ?: startOfEpoch,
                        to = clientStats[lastSeen]?.toString() ?: startOfEpoch,
                        timesSeen = clientStats[occurrences].toInt()
                    )
                }
            }
            .sortedBy { it.ip }
    }

    fun addIpNow(ip: String): Occurrences = transaction(database) {
        val clients = Client.find { Clients.ip eq ip }
        val client = clients.elementAtOrNull(0) ?: Client.new { this.ip = ip }

        val newSeen = Seen.new { seenOn = DateTime() }
        newSeen.client = client

        val clientStats = clientStats(client.id.value).first()
        Occurrences(
            ip = ip,
            from = clientStats[firstSeen]?.toString() ?: startOfEpoch,
            to = clientStats[lastSeen]?.toString() ?: startOfEpoch,
            timesSeen = clientStats[occurrences].toInt()
        )
    }

    fun countOccurrencesSince(ip: String, timeSpecification: String): Occurrences {
        val currentDateTime = DateTime()
        val dateTimeAdjuster = DateTimeAdjuster(currentDateTime)
        val from = dateTimeAdjuster.by(timeSpecification)

        return transaction(database) {
            val occurrencesSince = (Clients innerJoin Seens)
                .slice(occurrences)
                .select { Op.build { Clients.ip eq ip } and Op.build { Seens.seenOn greaterEq from } }
                .first()

            Occurrences(
                ip = ip,
                from = from.toString(),
                to = currentDateTime.toString(),
                timesSeen = occurrencesSince[occurrences].toInt()
            )
        }
    }

    private fun clientStats(clientId: Int) = Seens
        .slice(firstSeen, lastSeen, occurrences)
        .select(Op.build { Seens.client eq clientId })

    private companion object {
        val firstSeen = Seens.seenOn.min().alias("firstSeen")
        val lastSeen = Seens.seenOn.max().alias("lastSeen")
        val occurrences = Seens.seenOn.count().alias("occurrences")
        val startOfEpoch = DateTime(0).toString()
    }
}