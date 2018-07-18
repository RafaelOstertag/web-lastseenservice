package ch.guengel.webtools

import org.joda.time.DateTime
import org.joda.time.Days.days
import org.joda.time.Period.*
import org.joda.time.ReadablePeriod

/**
 * Given a DateTime, it calculates a new DateTime based on a time specification.
 *
 * The time specification has following form:
 *
 *     <days>d<hours>h<minutes>m<seconds>s
 *
 * For instance
 *
 *     1d7s
 *
 * would adjust the given DateTime by subtracting one day and seven seconds.
 */
class DateTimeAdjuster(private val current: DateTime) {
    private val regex =
        Regex("(?:(?<DAYS>-?[1-9][0-9]?)d|(?<HOURS>-?(?:[1-9]|1[0-9]|2[0-3]))h|(?<MINUTES>-?(?:[1-9]|[1-5][0-9]))m|(?<SECONDS>-?(?:[1-9]|[1-5][0-9]))s|\\s+)+")

    fun by(timeSpecString: String): DateTime {
        val match = regex.matchEntire(timeSpecString) ?: throw IllegalArgumentException("Illegal time spec pattern")

        val durations = resolveMatchToPeriods(match)

        return durations.fold(current, { acc, readablePeriod -> acc.minus(readablePeriod) })
    }

    private fun resolveMatchToPeriods(match: MatchResult): List<ReadablePeriod> {
        return Converters
            .values()
            .map {
                it.converter()(match.groups[it.name]?.value)
            }
    }

}


internal enum class Converters {
    DAYS {
        override fun converter(): (String?) -> ReadablePeriod {
            return { x -> if (x == null) days(0) else days(x.toInt()) }
        }
    },
    HOURS {
        override fun converter(): (String?) -> ReadablePeriod {
            return { x -> if (x == null) hours(0) else hours(x.toInt()) }
        }
    },
    MINUTES {
        override fun converter(): (String?) -> ReadablePeriod {
            return { x -> if (x == null) minutes(0) else minutes(x.toInt()) }
        }
    },
    SECONDS {
        override fun converter(): (String?) -> ReadablePeriod {
            return { x -> if (x == null) seconds(0) else seconds(x.toInt()) }
        }
    };

    abstract fun converter(): (String?) -> ReadablePeriod
}