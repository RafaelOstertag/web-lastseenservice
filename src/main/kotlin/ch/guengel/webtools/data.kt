package ch.guengel.webtools

import org.joda.time.DateTime

data class Occurrences(val ip: String, val from: DateTime, val to: DateTime, val timesSeen: Int)

