package ch.guengel.webtools.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Seens : IntIdTable() {
    val client = reference("client", Clients)
    val seenOn = datetime("on")
}

class Seen(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Seen>(Seens)

    var client by Client referencedOn Seens.client
    var seenOn by Seens.seenOn
}