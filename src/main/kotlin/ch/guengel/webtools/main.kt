package ch.guengel.webtools

import ch.guengel.webtools.serviceregistry.Consul
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.net.DatagramSocket
import java.net.InetAddress
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

fun registerService(serviceRegistryAddress: String, servicePort: Int) {
    GlobalScope.launch(Dispatchers.IO) {
        val serviceRegistry = Consul(serviceRegistryAddress)
        serviceRegistry.register(getIp(), servicePort)
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
    val commandLineEnvironment = commandLineEnvironment(args)
    val serviceRegistryAddress = commandLineEnvironment.config.property("serviceregistry.address").getString()
    val servicePort = commandLineEnvironment.config.property("ktor.deployment.port").getString().toInt()
    registerService(serviceRegistryAddress, servicePort)

    embeddedServer(Netty, commandLineEnvironment).start(true)
}

