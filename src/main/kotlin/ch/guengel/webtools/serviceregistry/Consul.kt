package ch.guengel.webtools.serviceregistry

import com.ecwid.consul.v1.ConsulClient
import com.ecwid.consul.v1.agent.model.NewService
import org.slf4j.LoggerFactory

private const val serviceName = "lastseen"

class Consul(private val consulAddress: String) : ServiceRegistry {
    private val logger = LoggerFactory.getLogger("ServiceRegistry")
    private val consulClient: ConsulClient = ConsulClient(consulAddress)

    override fun register(ip: String, port: Int) {
        val service = makeService(ip, port)
        consulClient.agentServiceRegister(service)
        logger.info(
            "Registered service '{}' as {}:{} with '{}'",
            serviceName,
            ip, port,
            consulAddress
        )
    }

    private fun makeService(ip: String, port: Int): NewService {
        val service = NewService()
        service.name = serviceName
        service.port = port
        service.check = makeCheck(ip, port)
        service.address = ip

        return service
    }

    private fun makeCheck(ip: String, port: Int): NewService.Check? {
        val check = NewService.Check()

        check.deregisterCriticalServiceAfter = "30s"
        check.interval = "10s"
        check.http = "http://$ip:$port/health"

        return check
    }
}