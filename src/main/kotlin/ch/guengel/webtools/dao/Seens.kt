package ch.guengel.webtools.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object Seens : IntIdTable() {
    val client = reference("client", Clients)
    val seenOn = datetime("seen_on")
}

class Seen(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Seen>(Seens)

    var client by Client referencedOn Seens.client
    var seenOn by Seens.seenOn
}