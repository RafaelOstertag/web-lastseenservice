package ch.guengel.webtools.services

import ch.guengel.lastseenservice.HealthGrpc
import ch.guengel.lastseenservice.HealthOuterClass
import io.grpc.Status
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.slf4j.LoggerFactory

class HealthGrpcService : HealthGrpc.HealthImplBase() {

    override fun check(request: HealthOuterClass.HealthCheckRequest?,
                       responseObserver: StreamObserver<HealthOuterClass.HealthCheckResponse>?) {

        when (request?.service) {
            "" -> sendSingleResponse(makeResponse(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING),
                    responseObserver)
            serviceName -> sendSingleResponse(makeResponse(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING),
                    responseObserver)
            else ->
                sendErrorStatus(Status.NOT_FOUND, responseObserver)
        }
        logger.info("Sent health response")
    }

    override fun watch(request: HealthOuterClass.HealthCheckRequest?,
                       responseObserver: StreamObserver<HealthOuterClass.HealthCheckResponse>?) {
        if (request?.service != "" && request?.service != serviceName) {
            sendErrorStatus(Status.NOT_FOUND, responseObserver)
            return
        }

        GlobalScope.launch(watchCoroutineContext) {
            val response = makeResponse(HealthOuterClass.HealthCheckResponse.ServingStatus.SERVING)
            try {
                while (true) {
                    responseObserver?.onNext(response)
                    logger.debug("Send streaming health response")
                    delay(1_000)
                }
            } catch (e: Exception) {
                logger.info("Client disconnected health watch stream", e)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HealthGrpcService::class.java)
        private const val serviceName = "ch.guengel.lastseenservice.LastSeen"
        private val watchCoroutineContext = newFixedThreadPoolContext(5, "watch-context")
    }
}

private fun sendSingleResponse(response: HealthOuterClass.HealthCheckResponse,
                               responseObserver: StreamObserver<HealthOuterClass.HealthCheckResponse>?) {
    responseObserver?.onNext(response)
    responseObserver?.onCompleted()
}

private fun sendErrorStatus(status: Status, responseObserver: StreamObserver<HealthOuterClass.HealthCheckResponse>?) =
        responseObserver?.onError(status.asRuntimeException())

private fun makeResponse(status: HealthOuterClass.HealthCheckResponse.ServingStatus) = HealthOuterClass.HealthCheckResponse.newBuilder().setStatus(
        status).build()