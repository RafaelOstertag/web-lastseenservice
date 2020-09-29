package ch.guengel.webtools

import ch.guengel.webtools.grpc.GrpcServer
import ch.guengel.webtools.services.HealthGrpcService
import ch.guengel.webtools.services.LastSeenGrpcService
import ch.guengel.webtools.services.LastSeenService
import org.slf4j.LoggerFactory
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*

fun printGitVersion() {
    val logger = LoggerFactory.getLogger("git-info")

    try {
        val properties = Properties()
        val gitPropertiesStream = object {}.javaClass.getResourceAsStream("/git.properties")
        gitPropertiesStream.use {
            properties.load(gitPropertiesStream)
            properties.forEach { name, value -> logger.info("$name: $value") }
        }
    } catch (e: Throwable) {
        logger.warn("Unable to read git.properties: {}", e.message ?: "null")
    }
}

private fun getIp(): String {
    val datagramSocket = DatagramSocket()
    val address = InetAddress.getByName("8.8.8.8")
    return datagramSocket.use {
        it.connect(address, 80)
        it.localAddress.hostAddress
    }
}

fun main(args: Array<String>) {
    printGitVersion()

    val databaseConnection = DatabaseConnection(Configuration.databaseUrl,
            Configuration.databaseUsername,
            Configuration.databasePassword)

    val serverPort = Configuration.port

    val lastSeenService = LastSeenService(databaseConnection.database)
    val lastSeenGrpcService = LastSeenGrpcService(lastSeenService)

    val server = GrpcServer(serverPort, lastSeenGrpcService, HealthGrpcService())
    server.runAndBlock()
}

