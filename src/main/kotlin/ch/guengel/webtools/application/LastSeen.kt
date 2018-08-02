package ch.guengel.webtools.application

import ch.guengel.webtools.services.LastSeenService
import io.ktor.application.Application
import io.ktor.routing.routing


fun Application.lastSeen() {
    val dbUrl = environment.config.property("database.url").getString()
    val dbUsername = environment.config.property("database.username").getString()
    val dbPassword = environment.config.property("database.password").getString()

    val databaseConnection = DatabaseConnection(dbUrl, dbUsername, dbPassword)

    val lasSeenService = LastSeenService(databaseConnection.database)

    routing {
        putLastSeenRoute(lasSeenService)
        getLastSeenRoute(lasSeenService)
    }
}

