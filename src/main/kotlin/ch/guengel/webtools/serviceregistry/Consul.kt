package ch.guengel.webtools.serviceregistry

import com.google.common.net.HostAndPort
import com.orbitz.consul.model.agent.ImmutableRegCheck
import com.orbitz.consul.model.agent.ImmutableRegistration
import org.slf4j.LoggerFactory

private const val serviceName = "lastseen"

class Consul(private val consulAddress: String) : ServiceRegistry {
    private val client = com.orbitz.consul.Consul.builder()
            .withHostAndPort(HostAndPort.fromString(consulAddress))
            .build()

    override fun register(ip: String, port: Int) {
        val service = ImmutableRegistration.builder()
                .id(serviceName)
                .name(serviceName)
                .port(port)
                .address(ip)
                .check(makeCheck(ip, port))
                .build()

        val agentClient = client.agentClient()
        agentClient.register(service)

        logger.info(
                "Registered service '{}' as {}:{} with '{}'",
                serviceName,
                ip, port,
                consulAddress)
    }

    private fun makeCheck(ip: String, port: Int) = ImmutableRegCheck.builder()
            .grpc("$ip:$port")
            .grpcUseTls(false)
            .deregisterCriticalServiceAfter("30s")
            .interval("10s")
            .build()

    companion object {
        private val logger = LoggerFactory.getLogger(Consul::class.java)
    }
}