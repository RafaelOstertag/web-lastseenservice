package ch.guengel.webtools.application

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

private data class HealthData(val status: String)

fun Route.getHealthRoute() {
    get("/health") {
        call.respond(HealthData("ok"))
    }

}