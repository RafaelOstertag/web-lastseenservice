package ch.guengel.webtools

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class LastSeenService(private val database: Database) {
    fun getAll(): List<Occurrences> {
        return Client.all()
            .map {
                with(it) {
                    val latest = latestSeen()
                    val earliest = earliestSeen()
                    Occurrences(ip = ip, timesSeen = seens.count(), from = earliest, to = latest)
                }
            }
            .sortedBy { it.ip }
    }

    private fun Client.earliestSeen() = seens.fold(DateTime()) { acc, seen -> minOf(acc, seen.seenOn) }

    private fun Client.latestSeen() = seens.fold(DateTime(0L)) { acc, seen -> maxOf(acc, seen.seenOn) }

    fun addIpNow(ip: String): Occurrences {
        val clients = Client.find { Clients.ip eq ip }
        val client: Client = transaction {
            val client = clients.elementAtOrNull(0) ?: Client.new { this.ip = ip }

            val newSeen = Seen.new { seenOn = DateTime() }
            newSeen.client = client
            client
        }

        return Occurrences(
            ip = ip,
            from = client.earliestSeen(),
            to = client.latestSeen(),
            timesSeen = client.seens.count()
        )
    }

    fun countOccurrencesSince(ip: String, timeSpecification: String): Occurrences {
        return Occurrences("", DateTime(), DateTime(), 2)
    }
}

