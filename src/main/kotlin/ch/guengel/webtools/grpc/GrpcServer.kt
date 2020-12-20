package ch.guengel.webtools.grpc

import ch.guengel.webtools.services.HealthGrpcService
import ch.guengel.webtools.services.LastSeenGrpcService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class GrpcServer(
    serverBuilder: ServerBuilder<*>,
    private val port: Int,
    lastSeenGrpcService: LastSeenGrpcService,
    healthGrpcService: HealthGrpcService
) {

    private val server: Server = serverBuilder
        .addService(lastSeenGrpcService)
        .addService(healthGrpcService)
        .build()

    constructor(port: Int, lastSeenGrpcService: LastSeenGrpcService, healthGrpcService: HealthGrpcService) : this(
        ServerBuilder.forPort(port),
            port,
            lastSeenGrpcService,
            healthGrpcService)

    private fun start() {
        server.start()
        logger.info("Server started, listening on port $port")

        Runtime.getRuntime().addShutdownHook(Thread {
            fun run() {
                println("*** Shutting down gRPC server")
                try {
                    stop()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                println("*** gRPC server shut down")
            }
        })
    }

    private fun stop() {
        server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    fun runAndBlock() {
        start()
        server.awaitTermination()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GrpcServer::class.java)
    }
}