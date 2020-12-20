package ch.guengel.webtools.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Clients : IntIdTable() {
    val ip = varchar("ip", 45).index(isUnique = true)
}

class Client(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Client>(Clients)

    var ip by Clients.ip
    val seens by Seen referrersOn Seens.client
}