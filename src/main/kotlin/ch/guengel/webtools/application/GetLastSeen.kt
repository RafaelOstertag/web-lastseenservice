package ch.guengel.webtools.application

import ch.guengel.webtools.dto.ErrorMessage
import ch.guengel.webtools.services.LastSeenService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.getLastSeenRoute(lastSeenService: LastSeenService) {
    get("/v1/lastseen/{ip}") {
        val ip = call.parameters["ip"]
        val since = call.request.queryParameters["since"]

        ip ?: throw IllegalArgumentException("Require ip")

        try {
            if (since != null) {
                call.respond(lastSeenService.countOccurrencesSince(ip, since))
            } else {
                call.respond(lastSeenService.countOccurrencesSince(ip, "1d"))
            }
        } catch (e: NoSuchElementException) {
            call.respond(HttpStatusCode.NotFound, ErrorMessage("IP $ip not found"))
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.BadRequest, ErrorMessage(e.message ?: "unknown reason"))
        }
    }

    get("/v1/lastseen") {
        call.respond(lastSeenService.getAll())
    }
}


