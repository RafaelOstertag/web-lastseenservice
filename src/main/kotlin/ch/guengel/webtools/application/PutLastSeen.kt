package ch.guengel.webtools.application

import ch.guengel.webtools.dto.ErrorMessage
import ch.guengel.webtools.services.LastSeenService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.put

fun Route.putLastSeenRoute(lastSeenService: LastSeenService) {
    put("/v1/lastseen/{ip}") {
        val ip = call.parameters["ip"]

        ip ?: throw IllegalArgumentException("require ip")

        try {
            call.respond(lastSeenService.addIpNow(ip))
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.BadRequest, ErrorMessage(e.message ?: "unknown reason"))
        }
    }
}

