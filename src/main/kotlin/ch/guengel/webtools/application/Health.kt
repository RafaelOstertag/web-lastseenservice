package ch.guengel.webtools.application

import io.ktor.application.Application
import io.ktor.routing.routing

fun Application.health() {
    routing {
        getHealthRoute()
    }
}
