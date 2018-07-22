package ch.guengel.webtools.application

import ch.guengel.webtools.services.LastSeenService
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.routing.routing
import org.slf4j.event.Level


fun Application.lastSeen() {
    val dbUrl = environment.config.property("database.url").getString()
    val dbUsername = environment.config.property("database.username").getString()
    val dbPassword = environment.config.property("database.password").getString()

    val databaseConnection = DatabaseConnection(dbUrl, dbUsername, dbPassword)

    val lasSeenService = LastSeenService(databaseConnection.database)

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        allowCredentials = false
        anyHost()
    }

    install(Compression)
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/v1/lastseen") }
    }

    install(ContentNegotiation) {
        jackson {}
    }

    routing {
        putLastSeenRoute(lasSeenService)
        getLastSeenRoute(lasSeenService)
    }
}

