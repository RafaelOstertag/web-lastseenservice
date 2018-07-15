package ch.guengel.webtools

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Clients : IntIdTable() {
    val ip = varchar("ip", 45)
}

object Seens : IntIdTable() {
    val client = reference("client", Clients)
    val seenOn = datetime("on")
}

class Client(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Client>(Clients)

    var ip by Clients.ip
    val seens by Seen referrersOn Seens.client
}

class Seen(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Seen>(Seens)

    var client by Client referencedOn Seens.client
    var seenOn by Seens.seenOn
}