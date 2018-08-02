package ch.guengel.webtools.serviceregistry

interface ServiceRegistry {
    fun register(ip: String, port: Int)
}