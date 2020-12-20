package ch.guengel.webtools

object Configuration {
    val port by lazy {
        System.getenv("SERVER_PORT")?.toInt() ?: 8080
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
    val poolSize: Int by lazy {
        System.getenv("DATABASE_POOL_SIZE")?.toInt() ?: 10
    }
}