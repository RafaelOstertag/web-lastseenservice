package ch.guengel.webtools

import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import java.util.*

fun printGitVersion() {
    val logger = LoggerFactory.getLogger("git-info")

    try {
        val properties = Properties()
        val gitPropertiesStream = properties::class.java.getResourceAsStream("/git.properties")
        gitPropertiesStream.use {
            properties.load(gitPropertiesStream)
            properties.forEach { name, value -> logger.info("$name: $value") }
        }
    } catch (e: Throwable) {
        logger.warn("Unable to read git.properties: {}", e.message ?: "null")
    }
}

fun main(args: Array<String>) {
    printGitVersion()
    embeddedServer(Netty, commandLineEnvironment(args)).start(true)
}

