package ch.guengel.webtools.application

import io.ktor.routing.Route
import io.ktor.routing.put
import org.jetbrains.exposed.sql.Database

fun Route.putLastRoute(db: Database) {
    put("/v1/lastseen/{ip}") {

    }
}

