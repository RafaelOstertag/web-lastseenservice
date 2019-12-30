package ch.guengel.webtools

object Configuration {
    val port by lazy {
        System.getenv("SERVER_PORT")?.toInt() ?: 8080
    }
    val consul by lazy {
        System.getenv("CONSUL") ?: "colossus.kruemel.home:8500"
    }
    val databaseUrl by lazy {
        System.getenv("DATABASE_URL") ?: ""
    }
    val databaseUsername by lazy {
        System.getenv("DATABASE_USERNAME") ?: ""
    }
    val databasePassword by lazy {
        System.getenv("DATABASE_PASSWORD") ?: ""
    }
}