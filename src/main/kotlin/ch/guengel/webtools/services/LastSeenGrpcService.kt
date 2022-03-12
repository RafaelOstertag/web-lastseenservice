package ch.guengel.webtools.services

import ch.guengel.lastseenservice.LastSeenGrpc
import ch.guengel.lastseenservice.Lastseen
import ch.guengel.webtools.dto.Occurrences
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory

private fun toLastSeenReply(occurrences: Occurrences) =
    Lastseen.LastSeenReply.newBuilder().setIp(occurrences.ip)
        .setFrom(occurrences.from)
        .setTo(occurrences.to)
        .setTimesSeen(occurrences.timesSeen)
        .build()

class LastSeenGrpcService(private val lastSeenService: LastSeenService) : LastSeenGrpc.LastSeenImplBase() {
    override fun getLastSeen(
        request: Lastseen.LastSeenRequest?,
        responseObserver: StreamObserver<Lastseen.LastSeenReply>?,
    ) {
        if (request == null) {
            val exception = Status.INVALID_ARGUMENT.asRuntimeException()
            responseObserver?.onError(exception)
            return
        }

        try {
            val result: Occurrences
            result = if (request.since.isBlank()) {
                lastSeenService.countOccurrencesSince(request.ip, "1d")
            } else {
                lastSeenService.countOccurrencesSince(request.ip, request.since)
            }
            responseObserver?.onNext(toLastSeenReply(result))
            responseObserver?.onCompleted()
            logger.info("Retrieved count for IP ${request.ip}")
        } catch (e: IllegalArgumentException) {
            logger.error("Error getting count for IP ${request.ip}", e)
            val exception = Status.fromCode(Status.Code.INVALID_ARGUMENT).withDescription(e.message).withCause(e)
                .asRuntimeException()
            responseObserver?.onError(exception)
        } catch (e: NoSuchElementException) {
            logger.error("Error getting count for IP ${request.ip}", e)
            val exception =
                Status.fromCode(Status.Code.NOT_FOUND).withDescription("IP not found").withCause(e).asRuntimeException()
            responseObserver?.onError(exception)
        } catch (e: Exception) {
            logger.error("Error getting count for IP ${request.ip}", e)
            val exception =
                Status.fromCode(Status.Code.UNKNOWN).withDescription(e.message).withCause(e).asRuntimeException()
            responseObserver?.onError(exception)
        }
    }

    override fun updateLastSeen(
        request: Lastseen.UpdateLastSeenRequest?,
        responseObserver: StreamObserver<Lastseen.LastSeenReply>?,
    ) {
        if (request == null) {
            val exception = Status.INVALID_ARGUMENT.asRuntimeException()
            responseObserver?.onError(exception)
            return
        }

        if (request.ip.isBlank()) {
            val exception =
                Status.fromCode(Status.Code.INVALID_ARGUMENT).withDescription("Require IP Address").asRuntimeException()
            responseObserver?.onError(exception)
        }

        try {
            val occurrences = lastSeenService.addIpNow(request.ip)
            responseObserver?.onNext(toLastSeenReply(occurrences))
            responseObserver?.onCompleted()
            logger.info("Update count for ${request.ip}")
        } catch (e: Exception) {
            val exception =
                Status.fromCode(Status.Code.UNKNOWN).withDescription(e.message).withCause(e).asRuntimeException()
            responseObserver?.onError(exception)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LastSeenGrpcService::class.java)
    }
}
